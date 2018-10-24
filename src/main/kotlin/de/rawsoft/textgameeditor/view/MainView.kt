package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.app.Styles
import javafx.scene.Group
import javafx.scene.control.Label
import tornadofx.View
import tornadofx.addClass
import tornadofx.plusAssign
import tornadofx.textfield

class MainView : View("Hello TornadoFX") {
    override val root = Group()
    val label = Label(title)

    init {
        with(root) {
            with(label) {
                addClass(Styles.heading)
                root.children += label
            }

            val textField = textfield {

            }
            this += textField
        }
    }
}