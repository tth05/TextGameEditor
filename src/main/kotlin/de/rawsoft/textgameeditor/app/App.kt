package de.rawsoft.textgameeditor.app

import de.rawsoft.textgameeditor.view.MainView
import javafx.scene.control.Tooltip
import javafx.util.Duration
import tornadofx.App





class App: App(MainView::class, Styles::class) {

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

            var TTBehaviourClass: Class<*>? = null
            val declaredClasses = Tooltip::class.java.declaredClasses
            for (c in declaredClasses) {
                if (c.canonicalName == "javafx.scene.control.Tooltip.TooltipBehavior") {
                    TTBehaviourClass = c
                    break
                }
            }
            if (TTBehaviourClass == null) {
                // abort
                return
            }
            val constructor = TTBehaviourClass.getDeclaredConstructor(
                    Duration::class.java, Duration::class.java, Duration::class.java, Boolean::class.javaPrimitiveType)
                    ?: // abort
                    return
            constructor.isAccessible = true
            val newTTBehaviour = constructor.newInstance(
                    Duration(openDelayInMillis), Duration(visibleDurationInMillis),
                    Duration(closeDelayInMillis), false)
                    ?: // abort
                    return
            val ttbehaviourField = Tooltip::class.java.getDeclaredField("BEHAVIOR")
                    ?: // abort
                    return
            ttbehaviourField.isAccessible = true

            // Cache the default behavior if needed.
            val defaultTTBehavior = ttbehaviourField.get(Tooltip::class.java)
            ttbehaviourField.set(Tooltip::class.java, newTTBehaviour)

        } catch (e: Exception) {
            println("Aborted setup due to error:" + e.message)
        }

    }
}

