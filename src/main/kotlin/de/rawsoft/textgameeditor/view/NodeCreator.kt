package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.game.ChangeVariableAction
import de.rawsoft.textgameeditor.game.GameAction
import de.rawsoft.textgameeditor.game.GameNodeModel
import de.rawsoft.textgameeditor.game.GoToAction
import javafx.scene.control.ListView
import tornadofx.*

class NodeCreator(val path: String, val onSave: (nodeModel: GameNodeModel) -> Unit) : View("Node Creator") {
    var listView: ListView<GameAction>? = null
    val actionFragment = hbox()
    val nodeModel: GameNodeModel = GameNodeModel()

    init {
        primaryStage.isResizable = false

        with(actionFragment) {
            spacing = 10.0
            vbox {
                spacing = 5.0
                menubutton(graphic = MaterialIconView(MaterialIcon.ADD).apply { size = "2em" }) {
                    item("GOTO") {
                        setOnAction {
                            val parent = listView!!.parent.parent
                            listView!!.parent.removeFromParent()
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
                            val parent = listView!!.parent.parent
                            listView!!.parent.removeFromParent()
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
                textfield(nodeModel.name)
            }
            field("Title:") {
                textfield(nodeModel.title)
            }
            field("Message:") {
                textarea(nodeModel.message)
            }
        }
        fieldset("Actions") {
            this += actionFragment
        }
        button("Save") {
            enableWhen(nodeModel.valid)
            setOnAction {
                onSave.invoke(nodeModel)
            }
        }
    }

    fun handleSwitch(action: GameAction) {
        action.root.parent.add(actionFragment)
        action.removeFromParent()
    }
}
