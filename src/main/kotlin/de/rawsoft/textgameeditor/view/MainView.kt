package de.rawsoft.textgameeditor.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import de.rawsoft.textgameeditor.controller.VariableController
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
                    treeview<String>(TreeItem("Start")) {
                        testFillTree(root)
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

                            button {
                                graphic = MaterialIconView(MaterialIcon.ADD).apply {
                                    size = "2em"
                                }

                                setOnMouseClicked {
                                    VariableCreator(tableview?.items).openModal()
                                }
                            }
                            button {
                                graphic = MaterialIconView(MaterialIcon.DELETE).apply {
                                    size = "2em"
                                }
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
            }
        }
        println(variableController.setPlaceholders("Der Spieler {Test} ist cooler als der Spieler {Test123}"))
    }

    private fun testFillTree(root: TreeItem<String>) {
        for (i in 0 until 5) {
            val item = TreeItem<String>("Test $i")
            for (j in 0 until 2) {
                item.children.add(TreeItem<String>("Test $j"))
            }
            root.children.add(item)
        }
    }
}