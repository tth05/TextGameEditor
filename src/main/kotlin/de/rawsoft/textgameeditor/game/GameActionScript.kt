package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.controller.VariableController

import javafx.beans.property.SimpleStringProperty
class GameActionScript(text: String = "", val variableController: VariableController) {
    val textProperty = SimpleStringProperty(text)
}