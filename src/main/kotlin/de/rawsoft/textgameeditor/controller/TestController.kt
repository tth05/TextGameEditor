package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.game.Variable
import tornadofx.*

class TestController : Controller() {
    val testVariables = observableList(Variable("One", "hello"), Variable("Two", "gay"))
}
