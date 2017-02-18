package pl.kpob.extensions

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView

/**
 * Created by krzysztofpobiarzyn on 09.01.2017.
 */
val RecyclerView.ViewHolder.ctx : Context
    get() = itemView.context

val RecyclerView.ViewHolder.res : Resources
    get() = itemView.context.resources