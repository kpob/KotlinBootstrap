package pl.kpob

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject

/**
 * Created by krzysztofpobiarzyn on 12.01.2017.
 */
object RxBus : AnkoLogger {

    val busSubject = SerializedSubject(PublishSubject.create<Any>())

    inline fun <reified T: Any> register(crossinline onNext: (T) -> Unit): Subscription {
        return busSubject
                .filter { it.javaClass == T::class.java }
                .map { it as T }
                .subscribe(
                        { onNext(it) },
                        { if(it is Exception) error { it.toString() } }
                )
    }

    fun post(event: Any) {
        busSubject.onNext(event)
    }
}