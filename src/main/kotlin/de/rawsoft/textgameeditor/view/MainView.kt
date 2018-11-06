package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import de.rawsoft.textgameeditor.game.GameNode
import de.rawsoft.textgameeditor.game.GameNodeModel
import de.rawsoft.textgameeditor.game.Variable
import de.rawsoft.textgameeditor.game.VariableModel
import javafx.scene.control.TabPane
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import tornadofx.*

class MainView : View("TextGameEditor") {
    override val root = borderpane()

    val variableModel: VariableModel by inject()

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()

    init {
        primaryStage.width = 1000.0
        primaryStage.height = 600.0
        primaryStage.isResizable = false
        with(root) {
            top = menubar {
                useMaxWidth = true
                menu("File") {
                    item("New")
                    item("Open")
                    item("Save")
                    item("Export")
                }
            }

            center = tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                tab("Nodes") {
                    treeview<GameNode> {
                        nodeController.refillTreeView(this)

                        contextMenu = contextmenu {
                            item("New") {
                                setOnAction {
                                    val selectedItem = this@treeview.selectionModel.selectedItem
                                    NodeCreator(selectedItem.value.path, GameNodeModel()) {
                                        val item = it.item
                                        item.path = "${this@treeview.selectionModel.selectedItem.value.path}.${it.name.value}"

                                        nodeController.nodes += item.path to item
                                        selectedItem.children.add(TreeItem(item))
                                    }.openModal()
                                }
                            }
                            item("Delete") {
                                setOnAction {
                                    if (this@treeview.selectionModel.selectedItem != null && this@treeview.selectionModel.selectedItem.value.path != "Start") {
                                        val selectedItemPath = this@treeview.selectionModel.selectedItem.value.path
                                        nodeController.removeNodeIf {
                                            it.path.startsWith(selectedItemPath)
                                        }
                                        val item = nodeController.getItemByPath(this@treeview, selectedItemPath.split("."))
                                        val parent = item!!.parent
                                        parent.children.remove(item)
                                    }
                                }
                            }
                        }

                        cellFormat {
                            this.text = this.treeItem.value.name
                        }
                    }
                }

                tab("Variables") {
                    hbox {
                        var tableview: TableView<Variable>? = null
                        vbox {
                            style {
                                spacing = 5.0.px
                                padding = box(5.0.px)
                            }
                            button(graphic = MaterialIconView(MaterialIcon.ADD).apply { size = "2em" }) {
                                setOnMouseClicked {
                                    VariableCreator(tableview?.items).openModal()
                                }
                            }
                            button(graphic = MaterialIconView(MaterialIcon.DELETE).apply { size = "2em" }) {
                                setOnMouseClicked {
                                    tableview!!.selectedItem?.apply { variableController.variables.remove(tableview!!.selectedItem) }
                                }
                            }
                        }
                        tableview = tableview(variableController.variables) {
                            column("Name", Variable::nameProperty).minWidth(150)
                            column("Value", Variable::valueProperty).remainingWidth()

                            prefWidth = 1000.0
                            bindSelected(variableModel)

                            contextmenu {
                                item("Delete").action {
                                    selectedItem?.apply { variableController.variables.remove(selectedItem) }
                                }
                            }

                            columnResizePolicy = SmartResize.POLICY
                        }
                        this += VariableEditor()
                    }
                }

                tab("Play") {
                    vbox {
                        val area = textarea {
                            isEditable = false
                            prefHeight = 1000.0

                            text = "wjfakjfakepgjapog"

                            style {
                                fontSize = 15.0.px
                            }
                        }

                        textfield {
                        }

                        spacing = 5.0
                        style {
                            padding = box(5.0.px)
                        }
                    }
                }
            }
        }
    }
}