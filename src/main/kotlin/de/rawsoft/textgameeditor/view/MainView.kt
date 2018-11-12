package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.controller.FileController
import de.rawsoft.textgameeditor.controller.NodeController
import de.rawsoft.textgameeditor.controller.VariableController
import de.rawsoft.textgameeditor.game.*
import javafx.scene.control.*
import javafx.stage.FileChooser
import tornadofx.*

class MainView : View("TextGameEditor") {
    override val root = borderpane()

    val variableModel: VariableModel by inject()

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()
    val fileController: FileController by inject()

    lateinit var treeView: TreeView<GameNode>

    var runner: Runner? = null

    init {
        primaryStage.width = 1000.0
        primaryStage.height = 600.0
        primaryStage.isResizable = false
        with(root) {
            top = menubar {
                useMaxWidth = true
                menu("File") {
                    setOnShown {
                        items.forEach {
                            it.isDisable = runner != null
                        }
                    }
                    item("New").setOnAction {
                        val files = chooseFile("Datei erstellen", arrayOf(FileChooser.ExtensionFilter("yml", "*.yml")), FileChooserMode.Save)
                        if (!files.isEmpty()) {
                            fileController.create(files[0].toPath())
                            nodeController.refillTreeView(treeView)
                        }
                    }
                    item("Open").setOnAction {
                        val files = chooseFile("Datei öffnen", arrayOf(FileChooser.ExtensionFilter("yml", "*.yml")), FileChooserMode.Single)
                        if (!files.isEmpty()) {
                            fileController.save()
                            fileController.load(files[0].toPath())
                            nodeController.refillTreeView(treeView)
                        }
                    }
                    item("Save").setOnAction {
                        var files = listOf(fileController.currentFile?.toFile())
                        if (fileController.currentFile == null)
                            files = chooseFile("Datei speichern", arrayOf(FileChooser.ExtensionFilter("yml", "*.yml")), FileChooserMode.Save)
                        if (!files.isEmpty() && files[0] != null) {
                            fileController.currentFile = files[0]!!.toPath()
                            fileController.save()
                        }
                    }
                    item("Export")
                }
            }

            center = tabpane {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                tab("Nodes") {
                    treeView = treeview<GameNode> {
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
                            playButton = button(graphic = MaterialIconView(MaterialIcon.PLAY_ARROW).apply { size = "2em" }) {
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

                            for (node in lookupAll(".scroll-pane")) {
                                if (node is ScrollPane) {
                                    val pane = node
                                    this.setOnScroll {
                                        val deltaY = it.deltaY * 10 // *6 to make the scrolling a bit faster
                                        val width = pane.content.boundsInLocal.width
                                        val vvalue = pane.vvalue
                                        pane.vvalue = vvalue + -deltaY / width
                                    }
                                }
                            }

                            textProperty().onChange {
                                this.scrollTopProperty().value = Double.MIN_VALUE
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