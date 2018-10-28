package de.rawsoft.textgameeditor.game

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Variable(name: String, value: String){
    private val validOperations = listOf("addtofront", "addtoback")

    val nameProperty = SimpleStringProperty(name)
    val name: String by nameProperty

    val valueProperty = SimpleStringProperty(value)
    var value: String by valueProperty

    fun isValidValue(value: String?): Boolean {
        return false
    }

    fun setValue(value: String?): Boolean {
        this.value = value ?: ""
        return true
    }

    fun getValidOperations(): List<String> {
        return validOperations
    }

    fun isValidOperation(operation: String, param: Any?) : Boolean {
        return getValidOperations().contains(operation)
    }

    fun executeOperation(operation: String, param: Any?) {
        this.value = if(operation == "addtofront") param.toString() + this.value else this.value + param.toString()
    }

    fun toString(`object`: String): String {
        return `object`
    }

    fun fromString(string: String?): String {
        return string ?: ""
    }
}

class VariableModel : ItemViewModel<Variable>() {
    val name = bind { item?.nameProperty }
    val value = bind { item?.valueProperty }
}


