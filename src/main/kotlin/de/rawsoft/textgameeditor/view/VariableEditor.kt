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
                textfield(model.name).validator {
                    if (!it.isNullOrBlank()) null else error("Ungültiger Name")
                }
            }

            field("Value") {
                textfield(model.value).validator {
                    if (model.item != null && model.item.isValidValue(it)) null else error("Ungültiger Wert")
                }
            }
        }
        button("Save") {
            enableWhen(model.valid)
            setOnAction {
                model.commit()
            }
        }

        model.validate(decorateErrors = true)
    }
}