package de.rawsoft.textgameeditor.app

import de.rawsoft.textgameeditor.view.MainView
import javafx.scene.control.Tooltip
import javafx.util.Duration
import tornadofx.App
import java.util.*


class App: App(MainView::class) {

    init {
        //Change global tooltip behaviour
        setupCustomTooltipBehavior(300.0, 5000.0, 200.0)
    }

    /**
     * Tooltip behavior is controlled by a private class javafx.scene.control.Tooltip$TooltipBehavior.
     * All Tooltips share the same TooltipBehavior instance via a static private member BEHAVIOR.
     *
     * This hack constructs a custom instance of TooltipBehavior and replaces private member BEHAVIOR with
     * this custom instance.
     */
    private fun setupCustomTooltipBehavior(openDelayInMillis: Double, visibleDurationInMillis: Double, closeDelayInMillis: Double) {
        try {
            val behaviorClass = Arrays.stream(Tooltip::class.java.declaredClasses).filter { it.canonicalName == "javafx.scene.control.Tooltip.TooltipBehavior" }.findAny().get()

            val constructor = behaviorClass.getDeclaredConstructor(Duration::class.java, Duration::class.java, Duration::class.java, Boolean::class.javaPrimitiveType) ?: return
            constructor.isAccessible = true

            val behaviorInstance = constructor.newInstance(Duration(openDelayInMillis), Duration(visibleDurationInMillis), Duration(closeDelayInMillis), false) ?: return
            val behaviorField = Tooltip::class.java.getDeclaredField("BEHAVIOR") ?: return
            behaviorField.isAccessible = true

            behaviorField.set(Tooltip::class.java, behaviorInstance)
        } catch (e: Exception) {
            println("Error while changing the Tooltip:" + e.message)
        }

    }
}

