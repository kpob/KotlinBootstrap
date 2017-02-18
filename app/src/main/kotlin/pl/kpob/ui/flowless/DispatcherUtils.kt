package pl.kpob.ui.flowless

import flowless.Traversal
import android.view.LayoutInflater
import android.view.ViewGroup
import android.animation.Animator
import android.content.Context
import android.view.View
import flowless.Direction


/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
internal object DispatcherUtils {

    fun addViewToGroupForKey(direction: Direction, view: View, root: ViewGroup, animatedKey: LayoutKey?) {
        if (animatedKey?.animation() != null && animatedKey?.animation()?.showChildOnTopWhenAdded(direction)?.not() ?: false) {
            root.addView(view, 0)
        } else {
            root.addView(view)
        }
    }

    fun createAnimatorForViews(animatedKey: LayoutKey?, previousView: View?, newView: View, direction: Direction): Animator? {
        if (previousView == null) {
            return null
        }
        if (animatedKey?.animation() != null) {
            return animatedKey?.animation()?.createAnimation(previousView, newView, direction)
        }
        return null
    }

    fun createViewFromKey(traversal: Traversal, newKey: LayoutKey, root: ViewGroup, baseContext: Context): View {
        val internalContext = DispatcherUtils.createContextForKey(traversal, newKey, baseContext)
        val layoutInflater = LayoutInflater.from(internalContext)
        val newView = layoutInflater.inflate(newKey.layout(), root, false)
        return newView
    }


    fun createContextForKey(traversal: Traversal, newKey: LayoutKey, baseContext: Context): Context {
        return traversal.createContext(newKey, baseContext)
    }

    fun selectAnimatedKey(direction: Direction, previousKey: LayoutKey?, newKey: LayoutKey): LayoutKey {
        return if (direction === Direction.BACKWARD) previousKey ?: newKey else newKey
    }

}