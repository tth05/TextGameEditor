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

    val localVariables = mutableMapOf<String, Any>()

    val manager = ScriptEngineManager()
    val engine = manager.getEngineByName("JavaScript")

    var currentNode: GameNode = nodeController.nodes["start"]!!
    var endGame = false
    var runHadGoto = false

    val GOTO_FUNCTION = Consumer<String> {
        runHadGoto = true
        val node = nodeController.nodes[it]
        if (node != null)
            currentNode = node
    }

    val ENDGAME_FUNCTION = Consumer<String> {
        this.endGame(it)
    }

    init {
        engine.put("goto", GOTO_FUNCTION)
        engine.put("endGame", ENDGAME_FUNCTION)
        variableController.variables.forEach {
            println("Put ${it.name} with ${it.getValue()}")
            engine.put(it.name, it.getValue())
            localVariables[it.name] = it.getValue()
        }
        updateScreen()
    }

    fun onInput(input: String) {
        if (!input.isInt()) return
        val i = input.toInt()
        if (i - 1 > currentNode.children.size || i < 0) return
        currentNode = nodeController.nodes["${currentNode.path}.${currentNode.children[i - 1]}"]!!
        execute()
    }

    fun execute() {
        val script = currentNode.actionScript
        if (script != null && !script.textProperty.value.isEmpty()) {
            engine.eval(script.textProperty.value)
            variableController.variables.forEach {
                println("Get ${it.name} : ${engine.get(it.name)}")
                it.setValue(engine.get(it.name))
            }
        }
        if (!endGame) {
            updateScreen()
        } else {
            variableController.variables.forEach {
                it.setValue(localVariables[it.name]!!)
            }
        }
        if (runHadGoto && !endGame) {
            runHadGoto = false
            execute()
        }
    }

    fun updateScreen() {
        var text = currentNode.message
        text += "\n"
        for (i in 0.until(currentNode.children.size)) {
            text += "${i + 1}. " + nodeController.nodes[currentNode.path + "." + currentNode.children[i]]!!.title + "\n"
        }

        text = variableController.setPlaceholders(text)
        text += "\n"
        text += "---------------------\n"
        textArea.appendText(text)
    }

    fun endGame(message: String) {
        endGame = true
        textArea.appendText(message)
        onEndGame.invoke()
    }
}