package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.game.Variable
import tornadofx.*

class VariableController : Controller() {

    val variables = observableList<Variable>()
    val variablesSortedByNames get() = variables.sorted { o1, o2 -> o1.name.compareTo(o2.name) }

    companion object {
        val variablePattern = Regex("(\\{\\w+})")
    }

    fun getVariableByName(name: String): Variable? {
        return variables.filter { it.name == name }.getOrElse(0) { null }
    }

    fun setPlaceholders(value: String) : String {
        var returns = value
        variablePattern.findAll(returns).forEach { result ->
            val variable = getVariableByName(result.value.replace(Regex("[{}]"), ""))
            if (variable != null)
                returns = returns.replace(result.value, variable.value)
        }

        if (variablePattern.containsMatchIn(returns))
            return setPlaceholders(returns)
        return returns
    }
}