package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.controller.NodeController
import javafx.scene.paint.Color
import tornadofx.*

abstract class GameAction(var onSavePress: () -> Unit = {}, var onCancelPress: () -> Unit = {}) : Fragment() {

    val nodeController: NodeController by inject()

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
                        selectionModel.select("start")
                        selectionModel.selectedItemProperty().onChange {
                            if (it != null) gotoPath = it
                        }
                    }
                }
            }
            hbox {
                button("Save") {
                    setOnMouseClicked {
                        println("invoke save")
                        onSavePress.invoke()
                        println(onSavePress)
                    }
                }
                button("Cancel") {
                    setOnMouseClicked {
                        onCancelPress.invoke()
                    }
                }
            }

            style {
                borderColor += box(Color.GREY)
                borderWidth += box(1.0.px)
            }
        }
    }

    override fun toString(): String {
        return "GOTO $gotoPath"
    }
}
