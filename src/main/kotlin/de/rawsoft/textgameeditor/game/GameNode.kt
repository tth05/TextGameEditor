package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.controller.VariableController
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

    var actionScript: GameActionScript? = null

    val children = mutableListOf<String>()

    fun toConfigSection(section: ConfigurationSection) {
        section.set("name", name)
        section.set("title", title)
        section.set("message", message)
        if (actionScript != null)
            section.set("script", (actionScript as GameActionScript).textProperty.value)
    }

    companion object {
        fun fromConfigSection(section: ConfigurationSection, path: String, variableController: VariableController): GameNode {
            val node = GameNode(section.getString("name"), path, section.getString("title"), section.getString("message"))
            section.getSection("children").keys.forEach { node.children.add(it) }
            if (section.getString("script") != "")
                node.actionScript = GameActionScript(section.getString("script"), variableController)
            return node
        }
    }
}

class GameNodeModel(initialValue: GameNode = GameNode("", "", "", "")) : ItemViewModel<GameNode>(initialValue) {
    val name = bind { item?.nameProperty }
    val title = bind { item?.titleProperty }
    val message = bind { item?.messageProperty }
}