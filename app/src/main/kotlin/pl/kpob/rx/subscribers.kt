package pl.kpob.rx

import pl.kpob.extensions.hide
import pl.kpob.extensions.isVisible
import pl.kpob.extensions.show
import pl.kpob.ui.LoadingView
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by krzysztofpobiarzyn on 10.01.2017.
 */
open class DefaultSubscriber<T>(private val view: LoadingView? = null): Subscriber<T>() {

    private var error: ((Throwable?) -> Unit)? = null
    private var next: ((T) -> Unit)? = null
    private var complete: (() -> Unit)? = null


    fun error(f: (Throwable?) -> Unit) { error = f }
    fun next(f: (T) -> Unit) { next = f }
    fun complete(f: () -> Unit) { complete = f }

    init {
        view?.errorView?.hide()
    }


    override fun onError(e: Throwable?) {
        error?.invoke(e)
        view?.onFinishLoading()
        view?.errorView?.show()
    }

    override fun onCompleted() {
        complete?.invoke()
        view?.onFinishLoading()
        if(view != null && view.errorView != null && view.errorView?.isVisible() ?: false) {
            view.errorView?.hide()
        }
    }

    override fun onNext(t: T) {
        next?.invoke(t)
    }

}

fun <T> Observable<T>.subscribeOnUiThread(view: LoadingView? = null, init: DefaultSubscriber<T>.() -> Unit) : Subscription =
        observeOn(AndroidSchedulers.mainThread())
                .subscribe(DefaultSubscriber<T>(view).apply { init() })

