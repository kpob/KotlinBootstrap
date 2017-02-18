package pl.kpob.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.view.MotionEvent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import pl.kpob.RxBus
import pl.kpob.kotlinbootstrap.BuildConfig
import pl.kpob.rx.SetScreensEvent
import pl.kpob.ui.Screen
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.util.concurrent.TimeUnit

/**
 * Created by kpob on 1/27/16.
 */
fun Int.isOdd() = this.mod(2) == 1
fun Int.isEven() = this.mod(2) == 0
fun Int.format(digits: Int) = String.format("%0${digits}d%n", this)

fun Long.toMinutes() = TimeUnit.MILLISECONDS.toMinutes(this)
fun Long.toSeconds() = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
fun Long.toTime() = "${this.toMinutes()}:${String.format("%1$02d", this.toSeconds().toInt())}"

inline fun supportsLollipop(func: () -> Unit) =
        supportsVersion(Build.VERSION_CODES.LOLLIPOP, func)

inline fun supportsVersion(ver: Int, func: () -> Unit) {
    if(Build.VERSION.SDK_INT >= ver) {
        func.invoke()
    }
}


inline fun inReleaseMode(func: () -> Unit) {
    if(BuildConfig.BUILD_TYPE.contains("release")) {
        func()
    }
}

inline fun inDebugMode(func: () -> Unit) {
    if(BuildConfig.BUILD_TYPE.contains("debug")) {
        func()
    }
}

inline fun <T> injectBuildTypeValue(func: BuildTypeValue<T>.() -> Unit) : T {
    val btv = BuildTypeValue<T>().apply { func() }
    val bt = BuildConfig.BUILD_TYPE.toLowerCase()
    if(bt.contains("release")) {
        return btv.release!!.invoke()
    }
    return btv.debug!!.invoke()
}

class BuildTypeValue<T> {

    var debug: (() -> T)? = null
    var release: (() -> T)? = null

    fun release(func: () -> T) { release = func }
    fun debug(func: () -> T) { debug = func }
}

class Support<T> {
    var older: (() -> T)? = null
    var newer: (() -> T)? = null

    fun newer(init: () -> T)  { newer = init }
    fun older(init: () -> T) { older = init }
}

inline fun <T> support(ver: Int, init: Support<T>.() -> Unit) : T {
    val support = Support<T>().apply { init() }
    return if(Build.VERSION.SDK_INT >= ver) {
        support.newer!!.invoke()
    } else {
        support.older!!.invoke()
    }
}

inline fun <T> supportLollipop(init: Support<T>.() -> Unit) : T = support(Build.VERSION_CODES.LOLLIPOP, init)


fun getDeviceId(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID);

inline fun ObjectAnimator.onStart(crossinline func: () -> Unit) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) { func() }
    })
}

inline fun ObjectAnimator.onEnd(crossinline func: () -> Unit) {
    addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) { func() }
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    })
}

fun Intent.hasExtras(extras: List<String>) : Boolean = extras.all { hasExtra(it) }

inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }

fun <T> readJsonFromAssets(ctx: Context, filepath: String, func: (Reader) -> T) : T =
    BufferedReader(InputStreamReader(ctx.assets.open(filepath), "UTF-8")).use {
        func(it)
    }


fun MotionEvent.use(func: (MotionEvent) -> Unit) {
    MotionEvent.obtain(this).apply {
        func(this)
    }.recycle()
}

inline fun delay(delay: Long = 2500, crossinline func: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed( { func() }, delay)
}

fun List<Screen>.set() { RxBus.post(SetScreensEvent(this)) }

fun AnkoLogger.debugLog(message: () -> Any?) { inDebugMode { info(message) } }

inline fun <T> T.applyConditionally(condition: Boolean, block: T.() -> Unit): T {
    if(condition) {
        block()
    }
    return this
}

fun String.status(init: StringStatus.() -> Unit) {
    val status = StringStatus().apply { init() }
    if(isEmpty()) {
        status.empty()
    } else {
        status.nonEmpty(this)
    }
}

class StringStatus {

    var empty: () -> Unit = {}
    var nonEmpty: (String) -> Unit = {}

    fun empty(f: () -> Unit) {
        empty = f
    }

    fun nonEmpty(f: (String) -> Unit) {
        nonEmpty = f
    }
}