package pl.kpob.ui.flowless

import android.support.annotation.LayoutRes
import android.os.Parcelable


/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
interface LayoutKey : Parcelable {

    @LayoutRes fun layout(): Int

    fun animation(): FlowAnimation
}