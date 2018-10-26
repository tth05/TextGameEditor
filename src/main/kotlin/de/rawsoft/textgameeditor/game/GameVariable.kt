package de.rawsoft.textgameeditor.game

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class GameVariable<T>(name: String, value: T) {

    val nameProperty = SimpleStringProperty(name)
    val name: String by nameProperty

    val valueProperty = SimpleObjectProperty<T>(value)
    var value: T by valueProperty
}