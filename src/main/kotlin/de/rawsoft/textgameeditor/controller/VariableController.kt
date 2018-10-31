package de.rawsoft.textgameeditor.controller

import de.rawsoft.textgameeditor.game.Variable
import javafx.collections.FXCollections
import tornadofx.*

class VariableController : Controller() {

    val variables = FXCollections.observableArrayList<Variable>()

    init {
        variables += Variable("Test", "125352gw4")
        variables += Variable("Test123", "12535wqrqr")
        variables += Variable("Test1afaf23", "12535wqasrqr")
        variables += Variable("Tefafst123", "12535wqrdadqr")
    }

    companion object {
        val variablePattern = Regex("(\\{\\w+})")
    }

    fun getVariableByName(name: String) : Pair<String, Variable?> {
        val variable = variables.filter { it.name == name }.getOrElse(0) {null}
        return if (variable == null) Pair("", null) else (Pair(variable.name, variable))
    }

    fun setPlaceholders(value: String) : String {
        var returns = value
        variablePattern.findAll(returns).forEach { result ->
            returns = returns.replace(result.value, getVariableByName(result.value.replace(Regex("[{}]"), "")).second!!.value)
        }
        return returns
    }
}