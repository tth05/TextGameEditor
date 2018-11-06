package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.frequencyOf
import de.rawsoft.textgameeditor.game.ChangeVariableAction
import de.rawsoft.textgameeditor.game.GameAction
import de.rawsoft.textgameeditor.game.GameNodeModel
import de.rawsoft.textgameeditor.game.GoToAction
import javafx.scene.control.ListView
import tornadofx.*

class NodeCreator(val path: String, val nodeModel: GameNodeModel, val onSave: (nodeModel: GameNodeModel) -> Unit) : View("Node Creator") {
    lateinit var listView: ListView<GameAction>
    val actionFragment = hbox()
    val nodeController: NodeController by inject()

    init {
        primaryStage.isResizable = false

        with(actionFragment) {
            spacing = 10.0
            vbox {
                spacing = 5.0
                menubutton(graphic = MaterialIconView(MaterialIcon.ADD).apply { size = "2em" }) {
                    item("GOTO") {
                        setOnAction {
                            val parent = listView.parent.parent
                            listView.parent.removeFromParent()
                            val action = GoToAction()
                            action.onSavePress = {
                                nodeModel.actions.value.add(action)
                                handleSwitch(action)
                            }
                            action.onCancelPress = {
                                handleSwitch(action)
                            }
                            parent.add(action)
                        }
                    }
                    item("CHANGEVARIABLE") {
                        setOnAction {
                            val parent = listView.parent.parent
                            listView.parent.removeFromParent()
                            val action = ChangeVariableAction()
                            action.onSavePress = {
                                nodeModel.actions.value.add(action)
                                handleSwitch(action)
                            }
                            action.onCancelPress = {
                                handleSwitch(action)
                            }
                            parent.add(action)
                        }
                    }
                }
                button(graphic = MaterialIconView(MaterialIcon.DELETE).apply { size = "2em" }) {
                    setOnAction {
                        if(listView.selectedItem != null) {
                            listView.items.remove(listView.selectedItem)
                        }
                    }
                }
            }
            listView = listview(nodeModel.actions) {
                prefWidth = 500.0
                maxHeight = 100.0
                cellFormat {
                    this.text = this.item.toString()
                }
            }
        }
    }

    override val root = form {
        fieldset("Node") {
            field("Name:") {
                textfield(nodeModel.name).validator {
                    if(nodeController.nodes.keys.filter { it.startsWith(path) && it.frequencyOf('.') > path.frequencyOf('.') }.map { it.substring(it.lastIndexOf('.') + 1) }.any { it == nodeModel.name.value })
                        error("Diesen Namen gibt es bereits")
                    else if(it.isNullOrBlank())
                        error("Ungültiger Wert")
                    else
                        null
                }
            }
            field("Title:") {
                textfield(nodeModel.title).validator {
                    if(it.isNullOrBlank()) error("Ungültiger Wert") else null
                }
            }
            field("Message:") {
                textarea(nodeModel.message).validator {
                    if(it.isNullOrBlank()) error("Ungültiger Wert") else null
                }
            }
        }
        fieldset("Actions") {
            this += actionFragment
        }
        button("Save") {
            enableWhen(nodeModel.valid)
            setOnAction {
                nodeModel.commit()
                onSave.invoke(nodeModel)
                close()
            }
        }

        nodeModel.validate(decorateErrors = true)
    }

    fun handleSwitch(action: GameAction) {
        action.root.parent.add(actionFragment)
        action.removeFromParent()
    }
}
