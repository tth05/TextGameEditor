package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.controller.VariableController
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class GameNode(name: String, path: String?, title: String, message: String) {
    val messageProperty = SimpleStringProperty(message)
    var message by messageProperty

    val titleProperty = SimpleStringProperty(title)
    var title by titleProperty

    val pathProperty = SimpleStringProperty(path)
    var path by pathProperty

    val nameProperty = SimpleStringProperty(name)
    var name by nameProperty

    val actionScriptProperty = SimpleObjectProperty<GameActionScript>()
    val actionScript by actionScriptProperty

    val children = mutableListOf<String>()


    companion object {
        fun fromConfigSection(section: ConfigurationSection, variableController: VariableController): GameNode {
            val node = GameNode(section.getString("name"), null, section.getString("title"), section.getString("message"))
            section.getSection("children").keys.forEach { node.children.add(it) }
            node.actionScriptProperty.value = GameActionScript(section.getString("script"), variableController)
            return node
        }
    }
}

class GameNodeModel(initialValue: GameNode = GameNode("", "", "", "")) : ItemViewModel<GameNode>(initialValue) {
    val name = bind { item?.nameProperty }
    val title = bind { item?.titleProperty }
    val message = bind { item?.messageProperty }
}