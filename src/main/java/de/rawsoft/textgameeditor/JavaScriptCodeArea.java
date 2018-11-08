package de.rawsoft.textgameeditor;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptCodeArea extends CodeArea {

	private final String[] KEYWORDS = new String[] {
			"if", "else", "var", "let", "for",
			"case", "while", "return", "const",
			"continue"
	};

	private final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private final String OPERATOR_PATTERN = "[><+-/*=]";
	private final String PAREN_PATTERN = "[()]";
	private final String BRACE_PATTERN = "[{}]";
	private final String BRACKET_PATTERN = "[\\[\\]]";
	private final String SEMICOLON_PATTERN = ";";
	private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private final Pattern PATTERN = Pattern.compile(
			"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
					+ "|(?<PAREN>" + PAREN_PATTERN + ")"
					+ "|(?<BRACE>" + BRACE_PATTERN + ")"
					+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
					+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
					+ "|(?<STRING>" + STRING_PATTERN + ")"
					+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
					+ "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
	);

	public JavaScriptCodeArea() {
		setParagraphGraphicFactory(LineNumberFactory.get(this));
		Subscription cleanupWhenNoLongerNeedIt = this
				.multiPlainChanges()
				.successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(this.getText())));
		InputMap<KeyEvent> im = InputMap.consume(
				EventPattern.keyPressed(KeyCode.TAB),
				e -> this.replaceSelection("  ")
		);
		Nodes.addInputMap(this, im);
	}

	private StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder
				= new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass =
					matcher.group("KEYWORD") != null ? "keyword" :
							matcher.group("PAREN") != null ? "paren" :
									matcher.group("BRACE") != null ? "brace" :
											matcher.group("BRACKET") != null ? "bracket" :
													matcher.group("SEMICOLON") != null ? "semicolon" :
															matcher.group("STRING") != null ? "string" :
																	matcher.group("COMMENT") != null ? "comment" :
																			matcher.group("OPERATOR") != null ? "operator" : null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
