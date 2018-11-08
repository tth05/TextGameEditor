package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import javafx.scene.control.TextArea
import tornadofx.*
import java.util.function.Consumer
import javax.script.ScriptEngineManager

class Runner(val textArea: TextArea, val onEndGame: () -> Unit) : Component() {

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()

    val manager = ScriptEngineManager()
    val engine = manager.getEngineByName("JavaScript")

    var currentNode: GameNode = nodeController.nodes["start"]!!
    var endGame = false

    val GOTO_FUNCTION = Consumer<String> {
        val node = nodeController.nodes[it]
        if(node != null)
            currentNode = node
    }

    val ENDGAME_FUNCTION = Consumer<String> {
        this.endGame(it)
    }

    init {
        engine.put("goto", GOTO_FUNCTION)
        engine.put("endGame", ENDGAME_FUNCTION)
        updateScreen()
    }

    fun onInput(input: String) {
        if(!input.isInt()) return
        val i = input.toInt()
        if(i - 1 > currentNode.children.size || i < 0) return
        currentNode = nodeController.nodes["${currentNode.path}.${currentNode.children[i - 1]}"]!!
        execute()
    }

    fun execute() {
        val script = currentNode.actionScript
        if(script != null && !script.textProperty.value.isEmpty()) {
            script.neededVariables.forEach {
                engine.put(it, variableController.getVariableByName(it)!!.getValue())
            }
            engine.eval(script.textProperty.value)
            script.neededVariables.forEach {
                val variable = variableController.getVariableByName(it)!!
                variable.setValue(engine.get(it))
            }
        }
        if(!endGame) updateScreen()
    }

    fun updateScreen() {
        textArea.clear()
        var text = "${currentNode.message}\n"
        text = variableController.setPlaceholders(text)
        for (i in 0.until(currentNode.children.size)) {
            text += "${i + 1}. " + nodeController.nodes[currentNode.path + "." + currentNode.children[i]]!!.title + "\n"
        }

        text += "\n"
        textArea.text = text
    }

    fun endGame(message: String) {
        endGame = true
        textArea.text = message
        onEndGame.invoke()
    }
}