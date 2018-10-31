package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.controller.VariableController
import de.rawsoft.textgameeditor.game.VariableModel
import javafx.scene.layout.Priority
import tornadofx.*

class VariableEditor : View() {

    val model: VariableModel by inject()
    val variableController: VariableController by inject()

    override val root = form {
        minWidth = 300.0
        hboxConstraints { hGrow = Priority.ALWAYS }

        fieldset {
            field("Name") {
                textfield(model.name).validator {
                    if (!it.isNullOrBlank() && !variableController.variables
                                    .filter { variable -> variable.name != model.item.name }
                                    .any { variable -> variable.name.equals(it, ignoreCase = true) })
                        null else error("Ungültiger Name")
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