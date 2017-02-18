package pl.kpob.ui

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import pl.kpob.extensions.hide
import pl.kpob.extensions.show

/**
 * Created by krzysztofpobiarzyn on 11.01.2017.
 */
interface CustomView {

    val ctx: Context
}

interface LoadingView {

    val errorView: View?

    val progressBar: ProgressBar

    fun onFinishLoading() { progressBar.hide() }
    fun showLoadingProgress() { progressBar.show() }

}