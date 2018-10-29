package de.rawsoft.textgameeditor.game

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

open class Variable(name: String, value: String){
    open val validOperations = listOf("addtofront", "addtoback", "setto")

    val nameProperty = SimpleStringProperty(name)
    val name: String by nameProperty

    val valueProperty = SimpleStringProperty(value)
    var value: String by valueProperty

    open fun isValidValue(value: String?): Boolean {
        return true
    }

    open fun setValue(value: String?): Boolean {
        this.value = value ?: ""
        return true
    }

    open fun isValidOperation(operation: String, param: Any?) : Boolean {
        return validOperations.contains(operation)
    }

    open fun executeOperation(operation: String, param: Any?) {
        this.value = if(operation == "addtofront") param.toString() + this.value else this.value + param.toString()
    }
}

class IntVariable(name: String, value: String) : Variable(name, value) {

    override val validOperations: List<String> = listOf("add", "subtract", "multiply", "divide", "setto")

    override fun isValidValue(value: String?) : Boolean {
        return value != null && value != "" && value.isInt()
    }

    override fun setValue(value: String?): Boolean {
        return if(isValidValue(value)) super.setValue(value) else false
    }

    override fun isValidOperation(operation: String, param: Any?): Boolean {
        return super.isValidOperation(operation, param) && param is Int
    }

    override fun executeOperation(operation: String, param: Any?) {
        when(operation) {
            "add" -> this.value = (this.value.toInt() + param.toString().toInt()).toString()
            "subtract" -> this.value = (this.value.toInt() - param.toString().toInt()).toString()
            "multiply" -> this.value = (this.value.toInt() * param.toString().toInt()).toString()
            "divide" -> this.value = (this.value.toInt() / param.toString().toInt()).toString()
            //Make sure to still convert to int atleast once
            "setto" -> this.value = param.toString().toInt().toString()
        }
    }
}

class VariableModel(initialValue: Variable? = null) : ItemViewModel<Variable>(initialValue = initialValue) {
    val name = bind { item?.nameProperty }
    val value = bind { item?.valueProperty }
}


