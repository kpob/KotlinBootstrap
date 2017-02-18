package pl.kpob.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
fun ViewGroup.inflate(layout: Int, attachToRoot: Boolean = true): View = LayoutInflater.from(context).inflate(layout, this, attachToRoot)

operator fun ViewGroup.get(position: Int) : View = getChildAt(position)
