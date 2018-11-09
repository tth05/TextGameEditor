package de.rawsoft.textgameeditor.game

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

open class Variable(name: String, value: String){
    val nameProperty = SimpleStringProperty(name)
    val name: String by nameProperty

    val valueProperty = SimpleStringProperty(value)
    val value: String by valueProperty

    open fun isValidValue(value: String?) : Boolean = value != null

    open fun setValue(value: Any) {
        valueProperty.value = value.toString()
    }

    open fun getValue() : Any {
        return value
    }
}

class IntVariable(name: String, value: String) : Variable(name, value) {

    override fun isValidValue(value: String?): Boolean = super.isValidValue(value) && value!!.isInt()

    override fun setValue(value: Any) {
        when(value) {
            is String -> valueProperty.value = value.toInt().toString()
            is Double -> valueProperty.value = value.toInt().toString()
            is Number -> valueProperty.value = value.toInt().toString()
        }
    }

    override fun getValue(): Any {
        return value.toInt()
    }
}

class VariableModel(initialValue: Variable? = null) : ItemViewModel<Variable>(initialValue = initialValue) {
    val name = bind { item?.nameProperty }
    val value = bind { item?.valueProperty }
}


