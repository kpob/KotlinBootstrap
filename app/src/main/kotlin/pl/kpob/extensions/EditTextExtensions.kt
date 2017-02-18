package pl.kpob.extensions

import android.widget.EditText

/**
 * Created by krzysztofpobiarzyn on 17.01.2017.
 */
infix fun EditText.swap(other: EditText) {
    val t1 = text
    val t2 = other.text

    text = t2
    other.text = t1
}