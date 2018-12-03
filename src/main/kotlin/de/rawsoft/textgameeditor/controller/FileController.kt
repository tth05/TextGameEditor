package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.config.YamlConfiguration
import de.rawsoft.textgameeditor.game.GameNode
import de.rawsoft.textgameeditor.game.IntVariable
import de.rawsoft.textgameeditor.game.Variable
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Path

class FileController : Controller() {

    val variableController: VariableController by inject()
    val nodeController: NodeController by inject()

    var currentFile: Path? = null

    fun load(path: Path) {
        if (!Files.exists(path)) return
        currentFile = path

        val config = YamlConfiguration()
        val section = config.load(path.toFile())

        nodeController.nodes.clear()
        loadNodeRecursive(section.getSection("nodes.start"), "start")

        variableController.variables.clear()
        section.getSection("variables").keys.forEach {

            val value = section.get("variables.$it")
            if (value is Int) variableController.variables += IntVariable(it, value.toString())
            else variableController.variables += Variable(it, value.toString())
        }
    }

    /**
     * Loads all nodes from the file recursively starting with the *start* node
     */
    private fun loadNodeRecursive(section: ConfigurationSection, path: String) {
        val node = GameNode.fromConfigSection(section, path, variableController)
        nodeController.nodes += path to node
        node.children.forEach {
            loadNodeRecursive(section.getSection("children").getSection(it), "$path.$it")
        }
    }

    fun new() {

    }

    fun save() {
        if (currentFile == null || !Files.exists(currentFile)) return
        val config = YamlConfiguration()
        val section = config.load((currentFile as Path).toFile())
        saveNodeRecursive(section.getSection("nodes.start"), nodeController.nodes["start"]!!)

        variableController.variables.forEach {
            section.set("variables.${it.name}", it.getValue())
        }
        config.save(section, (currentFile as Path).toFile())
    }

    /**
     * Saves all nodes recursively starting with the *start* node
     */
    private fun saveNodeRecursive(section: ConfigurationSection, node: GameNode) {
        node.toConfigSection(section)
        node.children.forEach {
            saveNodeRecursive(section.getSection("children.$it"), nodeController.nodes["${node.path}.$it"]!!)
        }
    }

    fun create(path: Path) {
        save()
        if (!Files.exists(path)) Files.createFile(path)
        currentFile = path
        nodeController.nodes.clear()
        nodeController.nodes["start"] = GameNode("start", "start", "unused", "This is a cool game")
        variableController.variables.clear()
    }
}