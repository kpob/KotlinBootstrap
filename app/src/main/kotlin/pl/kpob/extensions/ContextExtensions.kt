package pl.kpob.extensions

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.os.PowerManager
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import flowless.Flow
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.powerManager
import java.util.*

/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
fun Context.showKeyboard(view: View) : Unit {
    inputMethodManager.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.hideKeyboard(views: Array<View>) : Boolean =
        views.any { inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0) }

fun Context.cancelMessageNotification(id: Int, tag: String? = null) {
    if(tag == null) notificationManager.cancel(id) else notificationManager.cancel(tag, id)
}


//fun Context.alert(init: AlertBuilder.() -> Unit) = AlertBuilder(this).apply { init() }

fun Context.color(id: Int) : Int = ContextCompat.getColor(this, id)

fun Context.drawable(id: Int) : Drawable = ContextCompat.getDrawable(this, id)


@Suppress("DEPRECATION")
fun Context.createScreenWakeLock() : PowerManager.WakeLock? =
        powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, javaClass.simpleName).apply {
            acquire()
        }

fun Context.releaseWakeLock(wakeLock: PowerManager.WakeLock) { wakeLock.release() }

inline fun <reified T: Service> Context.startService() {
    startService(intentFor<T>())
}



fun Context.shareWithImages(text: String, subject: String = "", uris:List <Uri> = listOf()) : Boolean {
    try {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            val uriList = ArrayList<Parcelable>()
            uris.forEach { uriList.add(it) }
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
        }
        startActivity(Intent.createChooser(intent, "Share"))
        return true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        return false
    }
}

fun Context.shareWithImage(text: String, subject: String = "", uri: Uri) : Boolean {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, "Share"))
        return true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        return false
    }
}

val Context.flow: Flow
    get() = Flow.get(this)

val Context.appWidgetManager: AppWidgetManager
    get() = AppWidgetManager.getInstance(this)