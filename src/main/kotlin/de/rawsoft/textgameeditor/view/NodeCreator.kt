package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.game.GameAction
import de.rawsoft.textgameeditor.game.GameNodeModel
import de.rawsoft.textgameeditor.game.GoToAction
import javafx.scene.control.ListView
import tornadofx.*

class NodeCreator(val nodeModel: GameNodeModel = GameNodeModel()) : View("My View") {
    var listView: ListView<GameAction>? = null
    val actionFragment = hbox()

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
                                action.root.parent.add(actionFragment)
                                action.removeFromParent()
                                primaryStage.sizeToScene()
                            }
                            action.onCancelPress = {
                                action.root.parent.add(actionFragment)
                                action.removeFromParent()
                                primaryStage.sizeToScene()
                            }
                            parent.add(action)
                            primaryStage.sizeToScene()
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
            field("Message:") {
                textarea(nodeModel.message)
            }
            field("Title:") {
                textfield(nodeModel.title)
            }
        }
        fieldset("Actions") {
            this += actionFragment
        }
    }
}
