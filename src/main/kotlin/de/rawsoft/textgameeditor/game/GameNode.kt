package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection

class GameNode(val name: String, val path: String?, val title: String, val message: String) {

    val children = mutableListOf<String>()
    val actions = mutableListOf<GameAction>()

    companion object {
        fun fromConfigSection(section: ConfigurationSection): GameNode {
            val node = GameNode(section.getString("name"), null, section.getString("title"), section.getString("message"))
            section.getSection("children").keys.forEach { node.children.add(it) }
            section.getSection("actions").keys.forEach { node.actions.add(GameAction.fromConfigSection(section.getSection("actions").getSection(it))) }
            return node
        }
    }
}