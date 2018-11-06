package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

abstract class GameAction(var onSavePress: () -> Unit = {}, var onCancelPress: () -> Unit = {}) : Fragment() {

    val nodeController: NodeController by inject()
    val variableController: VariableController by inject()

    companion object {
        val allActions = mapOf<String, Class<out GameAction>>("GOTO" to GoToAction::class.java/*, "CHANGE_VARIABLE" to ChangeVariableAction::class.java*/)
        fun fromConfigSection(section: ConfigurationSection): GameAction = GoToAction("")
    }

    //TODO: Abstract method for executing the action
}

class GoToAction(var gotoPath: String = "start") : GameAction() {
    override val root = vbox {
        form {
            fieldset("GoTo Action") {
                field("Jump to:") {
                    combobox(values = nodeController.sortedNodeKeys) {
                        selectionModel.select(gotoPath)
                        selectionModel.selectedItemProperty().onChange {
                            if (it != null) gotoPath = it
                        }
                    }
                }
            }
            hbox {
                button("Save") {
                    setOnMouseClicked {
                        onSavePress.invoke()
                    }
                }
                button("Cancel") {
                    setOnMouseClicked {
                        onCancelPress.invoke()
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "GOTO $gotoPath"
    }
}

class ChangeVariableAction(var variable: String = "", var operation: String = "", var value: String = "") : GameAction() {

    val validValue = SimpleBooleanProperty(false)

    override val root = vbox {
        form {
            fieldset("Change Variable Action") {
                field("Variable:") {
                    val variableBox = combobox(values = variableController.variablesSortedByNames) {
                        if (items.size > 0) {
                            selectionModel.select(if(variable == "") {
                                variable = items[0].name
                                items[0]
                            } else variableController.getVariableByName(variable))
                        }
                        selectionModel.selectedItemProperty().onChange {
                            if (it != null) variable = it.name
                            updateValidation()
                        }

                        cellFormat {
                            this.text = this.item.name
                        }
                    }
                    combobox(values = variableBox.selectedItem?.validOperations) {
                        if (items.size > 0) {
                            if(operation == "") operation = "setto"
                            selectionModel.select(operation)
                            updateValidation()
                        }
                        variableBox.selectionModel.selectedItemProperty().onChange {
                            items = it!!.validOperations.observable()
                        }
                        selectionModel.selectedItemProperty().onChange {
                            operation = it ?: ""
                            updateValidation()
                        }
                    }
                    textfield {
                        this.text = value
                        this.textProperty().onChange {
                            value = it ?: ""
                            updateValidation()
                        }
                    }
                }
            }
            hbox {
                button("Save") {
                    enableWhen(validValue)
                    setOnMouseClicked {
                        onSavePress.invoke()
                    }
                }
                button("Cancel") {
                    setOnMouseClicked {
                        onCancelPress.invoke()
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "$variable $operation \"$value\""
    }

    fun updateValidation() {
        validValue.value = variable != "" && operation != "" && variableController.getVariableByName(variable)!!.isValidValue(value)
    }
}