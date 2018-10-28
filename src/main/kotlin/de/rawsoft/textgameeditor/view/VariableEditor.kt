package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.game.VariableModel
import javafx.scene.layout.Priority
import tornadofx.*

class VariableEditor : View() {

    val model: VariableModel by inject()

    override val root = form {
        minWidth = 300.0
        hboxConstraints { hGrow = Priority.ALWAYS }

        fieldset {
            field("Name") {
                textfield(model.name)
            }

            field("Value") {
                textfield(model.value)
            }
        }
        button("Save").setOnAction {
            model.commit()
        }
    }
}