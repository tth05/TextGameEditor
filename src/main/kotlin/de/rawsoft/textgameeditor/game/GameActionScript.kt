package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.controller.VariableController
import javafx.beans.property.SimpleStringProperty

class GameActionScript(text: String = "", val variableController: VariableController) {

    val neededVariables = mutableListOf<String>()

    val textProperty = SimpleStringProperty(text)

    init {
        VariableController.variablePattern.findAll(text).forEach {
            val name = it.value.replace(Regex("[{}]"), "")
            val variable = variableController.getVariableByName(name)
            if(variable != null) neededVariables += name
        }
    }
}