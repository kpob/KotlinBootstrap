package pl.kpob.ui.flowless

import android.os.Parcelable
import flowless.KeyParceler

/**
 * Created by krzysztofpobiarzyn on 12.01.2017.
 */
class AppKeyParceler : KeyParceler {

    override fun toParcelable(key: Any): Parcelable = key as Parcelable

    override fun toKey(parcelable: Parcelable): Any = parcelable
}