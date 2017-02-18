package pl.kpob.ui

import android.content.Context
import android.os.Parcel
import flowless.Direction
import pl.kpob.RxBus
import pl.kpob.extensions.createParcel
import pl.kpob.extensions.supportLollipop
import pl.kpob.rx.ReplaceScreenEvent
import pl.kpob.rx.SetScreenEvent
import pl.kpob.ui.flowless.FlowAnimation
import pl.kpob.ui.flowless.LayoutKey

/**
 * Created by krzysztofpobiarzyn on 12.01.2017.
 */
interface Screen: LayoutKey {
    val layoutId: Int

    override fun animation(): FlowAnimation = FlowAnimation.SEGUE

    override fun layout(): Int = layoutId

    fun set(ctx: Context) = RxBus.post(SetScreenEvent(this))

    fun replace(ctx: Context, direction: Direction = Direction.REPLACE) = RxBus.post(ReplaceScreenEvent(this, direction))
}

interface TopLevelScreen: Screen {

    override fun animation(): FlowAnimation = supportLollipop {
        newer { FlowAnimation.REVEAL }

        older { FlowAnimation.CROSSFADE }
    }

}

interface TitledScreen: Screen {

    val screenTitle: Int

}

data class EmptyScreen(override val layoutId: Int = 0) : Screen {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel(::EmptyScreen)
    }

    constructor(parcelIn: Parcel) : this(parcelIn.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(layoutId)
    }

    override fun describeContents() = 0
    override fun equals(other: Any?): Boolean = other is EmptyScreen
    override fun hashCode(): Int = 31 * layoutId

}