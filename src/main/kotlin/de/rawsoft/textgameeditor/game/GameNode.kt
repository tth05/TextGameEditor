package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import javafx.beans.property.SimpleListProperty
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

    val actionsProperty = SimpleListProperty<GameAction>()
    val actions by actionsProperty

    val children = mutableListOf<String>()


    companion object {
        fun fromConfigSection(section: ConfigurationSection): GameNode {
            val node = GameNode(section.getString("name"), null, section.getString("title"), section.getString("message"))
            section.getSection("children").keys.forEach { node.children.add(it) }
            section.getSection("actions").keys.forEach { node.actions.add(GameAction.fromConfigSection(section.getSection("actions").getSection(it))) }
            return node
        }
    }
}

class GameNodeModel() : ItemViewModel<GameNode>() {
    val name = bind { item?.nameProperty }
    val title = bind { item?.nameProperty }
    val message = bind { item?.nameProperty }
    val actions = bind { item?.actionsProperty }
}