package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.controller.VariableController
import de.rawsoft.textgameeditor.game.IntVariable
import de.rawsoft.textgameeditor.game.Variable
import de.rawsoft.textgameeditor.game.VariableModel
import javafx.collections.ObservableList
import tornadofx.*

class VariableCreator(val items: ObservableList<Variable>?) : View("Variable erstellen") {

    val model = VariableModel(Variable("", ""))
    val variableController: VariableController by inject()

    override val root = form {
        minWidth = 300.0
        fieldset("Neue Variable erstellen") {
            field("Typ") {
                combobox(values = listOf("Text", "Number")) {
                    selectionModel.selectFirst()
                    setOnAction {
                        if(model.item !is IntVariable && selectedItem == "Int") model.item = IntVariable("", "")
                        if(model.item is IntVariable && selectedItem == "String") model.item = Variable("", "")
                    }
                }
            }

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
                items!!.add(model.item)
                close()
            }
        }

        model.validate(decorateErrors = true)
    }
}
