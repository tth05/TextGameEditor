package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.controller.VariableController

import javafx.beans.property.SimpleStringProperty
class GameActionScript(text: String = "", val variableController: VariableController) {

    val textProperty = SimpleStringProperty(text)

    val neededVariables: MutableList<String> get() {
        val list = mutableListOf<String>()
        VariableController.variablePattern.findAll(textProperty.value).forEach {
            val name = it.value.replace(Regex("[{}]"), "")
            val variable = variableController.getVariableByName(name)
            if (variable != null) list += name
        }
        return list
    }
}