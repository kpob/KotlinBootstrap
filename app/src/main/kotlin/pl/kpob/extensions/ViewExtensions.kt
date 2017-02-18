package pl.kpob.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import flowless.ActivityUtils
import flowless.Flow
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import pl.kpob.activity.FlowActivity
import pl.kpob.ui.TitledScreen


/**
 * Created by kpob on 3/18/16.
 */
fun View.isVisible() = visibility == View.VISIBLE

fun View.show() { visibility = View.VISIBLE }

fun View.hide() { visibility = View.GONE }

fun View.makeInvisible() { visibility = View.INVISIBLE }

fun View.getString(res: Int) = resources.getString(res)

fun View.getString(res: Int, vararg formatArgs: Any) = resources.getString(res, formatArgs)

inline fun onViews(views: List<View>, func: (View) -> Unit) {
    views.map { func(it) }
}

fun hideViews(views: List<View>) { onViews(views, View::hide) }

fun showViews(views: List<View>) { onViews(views, View::show) }

fun conditionalShowViews(views: List<View>, predicate: () -> Boolean) {
    if(predicate()) showViews(views) else hideViews(views)
}

fun View.slideExit(direction: Int = 0, func: () -> Unit = {}) {
    if (translationY == 0f) animate().setListener(object: Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) { }

        override fun onAnimationEnd(animation: Animator?) {
            func()
        }

        override fun onAnimationCancel(animation: Animator?) { }

        override fun onAnimationStart(animation: Animator?) { }

    }).translationY(if(direction==0) -height.toFloat() else height.toFloat())
}

fun View.slideEnter(direction: Int = 0, func: () -> Unit = {}) {
    if (if(direction == 0) translationY < 0f else translationY > 0f) animate().setListener(object: Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) { }

        override fun onAnimationEnd(animation: Animator?) { }

        override fun onAnimationCancel(animation: Animator?) { }

        override fun onAnimationStart(animation: Animator?) {
            func()
        }
    }).translationY(0f)
}

fun View.waitForMeasure(func: (v: View, w: Int, h: Int) -> Unit) {

    if (width > 0 && height > 0) {
        func(this, width, height)
        return
    }

    val listener = object : ViewTreeObserver.OnPreDrawListener {

        override fun onPreDraw(): Boolean {
            val observer = viewTreeObserver
            if (observer.isAlive) {
                observer.removeOnPreDrawListener(this)
            }

            func(this@waitForMeasure, width, height)
            return true
        }
    }

    viewTreeObserver.addOnPreDrawListener(listener)

}

inline fun View.onViewClick(id: Int, crossinline func: () -> Unit) = find<View>(id).onClick { func() }
fun View.showView(id: Int) = find<View>(id).show()
fun View.setText(id: Int, txt: CharSequence) {
    find<TextView>(id).text = txt
}

fun View.fadeIn(duration: Long, f:() -> Unit = {}) {
     ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
        setDuration(duration)
        onStart { f() }
        start()
    }
}

fun View.fadeOut(duration: Long, f:() -> Unit = {}) {
    ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
        setDuration(duration)
        onEnd { f() }
        start()
    }
}

fun View.hideKeyboard() = context.hideKeyboard(arrayOf(this))

fun View.showKeyboard() = context.showKeyboard(this)

fun View.flow() : Flow = context.flow

fun<T> View.key() : T? = Flow.getKey<T>(this)

fun<T: TitledScreen> View.title() : String = context.getString(Flow.getKey<T>(this)!!.screenTitle)

val View.activity: FlowActivity
    get() = ActivityUtils.getActivity(context) as FlowActivity

@Suppress("DEPRECATION")
fun TextView.textAppearance(resId: Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

inline fun View.useAttributes(attrs: AttributeSet, styleable: IntArray, func: (TypedArray) -> Unit) {
    val a = context.theme.obtainStyledAttributes(attrs, styleable, 0, 0)

    try {
        func(a)
    } finally {
        a.recycle()
    }
}

fun View.toast(message: CharSequence) {
    context.toast(message)
}

fun View.toast(id: Int) {
    context.toast(id)
}