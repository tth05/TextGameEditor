package de.rawsoft.textgameeditor.game

import de.rawsoft.textgameeditor.config.ConfigurationSection
import de.rawsoft.textgameeditor.controller.NodeController
import tornadofx.*

abstract class GameAction : Fragment() {

    val nodeController: NodeController by inject()

    companion object {
        val allActions = mapOf<String, Class<out GameAction>>("GOTO" to GoToAction::class.java/*, "CHANGE_VARIABLE" to ChangeVariableAction::class.java*/)
        fun fromConfigSection(section: ConfigurationSection): GameAction = GoToAction("")
    }

    abstract fun isValid(): Boolean

    //TODO: Abstract method for executing the action
}

class GoToAction(var gotoPath: String? = null) : GameAction() {
    override val root = vbox {
        label("GOTO Action")
        val combobox = combobox<String> {
            selectionModel.select("start")
            selectionModel.selectedItemProperty().onChange {
                if (it != null) gotoPath = it
            }
            items.addAll(nodeController.sortedNodeKeys)

            cellFormat {
                this.text = it.substring(it.lastIndexOf('.'))
            }
        }
    }

    override fun isValid(): Boolean {
        return gotoPath != null
    }
}
