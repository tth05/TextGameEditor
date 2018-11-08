package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.game.GameNode
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import tornadofx.*

class NodeController : Controller() {
    val nodes = mutableMapOf<String, GameNode>()
    val nodesSortedByKeys: MutableMap<String, GameNode>
        get() {
            //To sorted map showed weird behaviour. This is a workaround
            val list = nodes.keys.sortedWith(Comparator { o1, o2 ->
                o1.frequencyOf('.').compareTo(o2.frequencyOf('.'))
//            println("i: $i, o1: $o1, o2: $o2")
//            if(i == 1) -1 else if(i == -1) 1 else 0
            })
            val map = mutableMapOf<String, GameNode>()
            list.forEach {
                map += it to nodes[it]!!
            }
            return map
        }
    val sortedNodeKeys get() = nodes.keys.sorted()

    init {
        nodes["start"] = GameNode("Start", "start", "", "This is a cool game")
        nodes["start.option1"] = GameNode("option1", "start.option1", "1. Go Left", "You go left, what's next?")
        nodes["start.option2.test"] = GameNode("test", "start.option2.test", "Test", "You chose test")
        nodes["start.option2"] = GameNode("option2", "start.option2", "2. Go right", "You go right, what's next?")
    }

    fun refillTreeView(view: TreeView<GameNode>) {
        view.root = TreeItem(nodes.getOrDefault("start", GameNode("Start", "start", "A message", "A message")))
        val map = nodesSortedByKeys.filter { it.key != "start" }

        map.forEach {
            val node = getParentItemByPath(view, it.key.split("."))
            node?.children?.add(TreeItem<GameNode>(it.value))
        }
    }

    /**
     * Returns the second last node in the specified path
     * Given start.1.2.3 this method returns the node at start.1.2
     */
    fun getParentItemByPath(view: TreeView<GameNode>, parts: List<String>, i: Int = 1, currentNode: TreeItem<GameNode> = view.root): TreeItem<GameNode>? {
        if (i >= parts.size - 1) return currentNode
        for (node in currentNode.children) {
            if (node.value.path!!.substring(node.value.path!!.lastIndexOf('.') + 1) == parts[i])
                return getParentItemByPath(view, parts, i + 1, node)
        }
        return null
    }

    fun getItemByPath(view: TreeView<GameNode>, parts: List<String>, i: Int = 1, currentNode: TreeItem<GameNode> = view.root): TreeItem<GameNode>? {
        if (i >= parts.size) return currentNode
        for (node in currentNode.children) {
            if (node.value.path!!.substring(node.value.path!!.lastIndexOf('.') + 1) == parts[i])
                return getItemByPath(view, parts, i + 1, node)
        }
        return null
    }

    fun removeNodeIf(validator: (GameNode) -> Boolean) {
        val iterator = nodes.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if(validator.invoke(next.value)) iterator.remove()
        }
    }
}

fun String.frequencyOf(c: Char): Int {
    var f = 0
    this.forEach { if (it == c) f++ }
    return f
}