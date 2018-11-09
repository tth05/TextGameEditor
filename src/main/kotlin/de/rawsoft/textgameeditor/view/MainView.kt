package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import de.rawsoft.textgameeditor.game.*
import javafx.scene.control.*
import tornadofx.*

class MainView : View("TextGameEditor") {
    override val root = borderpane()

    val variableModel: VariableModel by inject()

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()

    var runner: Runner? = null

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

                                        //Add to nodes map
                                        nodeController.nodes += item.path to item
                                        //Add to TreeView
                                        val treeItem = TreeItem(item)
                                        selectedItem.children.add(treeItem)

                                        //Add to children of node parent
                                        val node = nodeController.getParentItemByPath(this@treeview, item.path.split("."))
                                        node!!.value.children.add(item.name)
                                    }.openModal()
                                }
                            }
                            item("Edit") {
                                setOnAction {
                                    val selectedItem = this@treeview.selectionModel.selectedItem
                                    NodeCreator(selectedItem.value.path, GameNodeModel(selectedItem.value)) {

                                    }.apply { checkName = false }.openModal()
                                }
                            }
                            item("Delete") {
                                setOnAction {
                                    if (this@treeview.selectionModel.selectedItem != null && this@treeview.selectionModel.selectedItem.value.path != "Start") {
                                        val selectedItemPath = this@treeview.selectionModel.selectedItem.value.path
                                        nodeController.removeNodeIf {
                                            it.path.startsWith(selectedItemPath)
                                        }

                                        //Remove from treeview
                                        val item = nodeController.getItemByPath(this@treeview, selectedItemPath.split("."))
                                        val parent = item!!.parent
                                        parent.children.remove(item)

                                        //Remove from children of parent
                                        parent.value.children.remove(item.value.name)
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
                        lateinit var playButton: Button
                        lateinit var stopButton: Button
                        lateinit var area: TextArea

                        hbox {
                            playButton = button(graphic = MaterialIconView(MaterialIcon.PLAYLIST_PLAY).apply { size = "2em" }) {
                                setOnMouseClicked {
                                    runner = Runner(area) {
                                        stopButton.isDisable = true
                                        runner = null
                                    }
                                    stopButton.isDisable = false
                                }
                            }
                            stopButton = button(graphic = MaterialIconView(MaterialIcon.STOP).apply { size = "2em" }) {
                                setOnMouseClicked {
                                    runner?.endGame("Interrupted")
                                    runner = null
                                    this.isDisable = true
                                }
                            }
                        }
                        stopButton.isDisable = true
                        playButton.enableWhen(stopButton.disabledProperty())

                        area = textarea {
                            isEditable = false
                            prefHeight = 1000.0

                            textProperty().onChange {
                                this.scrollTopProperty().value = Double.MAX_VALUE
                            }

                            style {
                                fontSize = 15.0.px
                            }
                        }

                        textfield {
                            setOnAction {
                                runner?.onInput(this.text)
                                this.text = ""
                            }
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