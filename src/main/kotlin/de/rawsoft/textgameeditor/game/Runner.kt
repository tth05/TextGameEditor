package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import javafx.scene.control.TextArea
import tornadofx.*
import java.util.function.Consumer

class Runner(val textArea: TextArea) : Component() {

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()

    var currentNode: GameNode = nodeController.nodes["start"]!!

    val GOTO_FUNCTION = Consumer<String> {
        val node = nodeController.nodes[it]
        if(node != null)
            currentNode = node
    }

    fun onInput(input: String) {

    }
}