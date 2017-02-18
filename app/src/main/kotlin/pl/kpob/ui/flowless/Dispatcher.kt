package pl.kpob.ui.flowless

import flowless.preset.SingleRootDispatcher
import flowless.TraversalCallback
import android.view.ViewGroup
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import flowless.Traversal
import flowless.ViewUtils


/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
class Dispatcher : SingleRootDispatcher() {

    override fun dispatch(traversal: Traversal, callback: TraversalCallback) {
        val root = rootHolder.root
        if (flowless.preset.DispatcherUtils.isPreviousKeySameAsNewKey(traversal.origin, traversal.destination)) { //short circuit on same key
            callback.onTraversalCompleted()
            onTraversalCompleted()
            return
        }
        val newKey = flowless.preset.DispatcherUtils.getNewKey<LayoutKey>(traversal)
        val previousKey = flowless.preset.DispatcherUtils.getPreviousKey<LayoutKey>(traversal)

        val direction = traversal.direction

        val previousView = root.getChildAt(0)
        flowless.preset.DispatcherUtils.persistViewToStateAndNotifyRemoval(traversal, previousView)

        val newView = DispatcherUtils.createViewFromKey(traversal, newKey, root, baseContext)
        flowless.preset.DispatcherUtils.restoreViewFromState(traversal, newView)

        val animatedKey = DispatcherUtils.selectAnimatedKey(direction, previousKey, newKey)
        DispatcherUtils.addViewToGroupForKey(direction, newView, root, animatedKey)

        configure(previousKey, newKey)

        ViewUtils.waitForMeasure(newView) { view, width, height ->
            val animator = DispatcherUtils.createAnimatorForViews(animatedKey, previousView, newView, direction)
            if (animator != null) {
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        finishTransition(previousView, root, callback)
                    }
                })
                animator.start()
            } else {
                finishTransition(previousView, root, callback)
            }
        }
    }

    private fun configure(previousKey: LayoutKey?, newKey: LayoutKey?) {}

    private fun finishTransition(previousView: View?, root: ViewGroup, callback: TraversalCallback) {
        if (previousView != null) {
            root.removeView(previousView)
        }
        callback.onTraversalCompleted()
        onTraversalCompleted()
    }

    private fun onTraversalCompleted() {}
}