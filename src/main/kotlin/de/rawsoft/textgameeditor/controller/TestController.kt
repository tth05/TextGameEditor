package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.game.GameVariable
import tornadofx.*

class TestController : Controller() {
    val testVariables = observableList(GameVariable("One", 1), GameVariable("Two", "gay"))
}
