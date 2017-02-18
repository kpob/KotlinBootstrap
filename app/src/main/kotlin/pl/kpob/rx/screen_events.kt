package pl.kpob.rx

import flowless.Direction
import pl.kpob.RxBus
import pl.kpob.ui.Screen

/**
 * Created by krzysztofpobiarzyn on 12.01.2017.
 */
interface Event {

    fun post() { RxBus.post(this) }

    companion object {
        inline fun <reified T: Event> register(crossinline f: (T) -> Unit) {
            RxBus.register(f)
        }
    }
}

data class SetScreenEvent(val screen: Screen) : Event
data class SetScreensEvent(val screens: List<Screen>, val direction: Direction = Direction.FORWARD) : Event
data class ReplaceScreenEvent(val screen: Screen, val direction: Direction = Direction.REPLACE) : Event
object GoBackEvent : Event
object OpenPopupWindowEvent : Event
object ClosePopupWindowEvent : Event


