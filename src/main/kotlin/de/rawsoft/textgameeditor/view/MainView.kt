package de.rawsoft.textgameeditor.view

import de.rawsoft.textgameeditor.controller.TestController
import de.rawsoft.textgameeditor.game.GameVariable
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.control.TreeItem
import tornadofx.*

class MainView : View("Hello TornadoFX") {
    override val root = borderpane()
    val label = Label(title)
    val testController: TestController by inject()

    init {
        primaryStage.width = 1000.0
        primaryStage.height = 600.0
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
                    tableview(testController.testVariables) {
                        column("Name", GameVariable<out Any>::nameProperty).minWidth(150)
                        column("Value", GameVariable<out Any>::value).remainingWidth()

                        contextmenu {
                            item("Delete").action {
                                selectedItem?.apply { testController.testVariables.remove(selectedItem) }
                            }
                        }

                        columnResizePolicy = SmartResize.POLICY
                    }
                }
            }
        }
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