package pl.kpob.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.WindowManager
import org.jetbrains.anko.ctx
import org.jetbrains.anko.inputMethodManager

/**
 * Created by kpob on 3/18/16.
 */

@SuppressLint("NewApi")
fun Activity.setStatusBarColor(colorResId: Int) {
    supportsLollipop {
        with(window) {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            statusBarColor = ctx.color(colorResId)
        }
    }
}

fun Activity.hideSoftKeyboard() {
    if(currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}

fun playServicesResult(init: PlayServicesResult.() -> Unit) : PlayServicesResult = PlayServicesResult().apply { init() }

class PlayServicesResult {
    lateinit var onSupported: () -> Unit
    lateinit var onNOTSupported: () -> Unit

    fun supported(func: () -> Unit) {
        onSupported = func
    }
    fun notSupported(func: () -> Unit) {
        onNOTSupported = func
    }

}

fun Activity.getPicture(oldWayRequestCode: Int, newWayRequestCode: Int) : Unit = when {
    Build.VERSION.SDK_INT < 19 -> {
        val i = Intent().apply {
            type = "image/* video/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(i, "Select"), oldWayRequestCode)
    }
    else -> {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/* video/*"
        }
        startActivityForResult(i, newWayRequestCode)
    }
}

fun Activity.takePicture(requestCode: Int, uri: Uri) : Unit  {
    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }
    startActivityForResult(i, requestCode)
}

fun Activity.recordVideo(requestCode: Int, uri: Uri) : Unit  {
    val i = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }
    if (i.resolveActivity(packageManager) != null) {
        startActivityForResult(i, requestCode)
    }
}

