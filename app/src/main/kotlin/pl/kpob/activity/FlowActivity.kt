package pl.kpob.activity

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import flowless.Direction
import flowless.Flow
import flowless.History
import flowless.preset.SingleRootDispatcher
import org.jetbrains.anko.AnkoLogger
import pl.kpob.RxBus
import pl.kpob.extensions.flow
import pl.kpob.extensions.get
import pl.kpob.rx.GoBackEvent
import pl.kpob.rx.ReplaceScreenEvent
import pl.kpob.rx.SetScreenEvent
import pl.kpob.rx.SetScreensEvent
import pl.kpob.ui.Screen
import pl.kpob.ui.TopLevelScreen
import pl.kpob.ui.flowless.AppKeyParceler
import pl.kpob.ui.flowless.BackPressHandler
import pl.kpob.ui.flowless.Dispatcher
import rx.Subscription


/**
 * Created by krzysztofpobiarzyn on 06.07.2016.
 */
abstract class FlowActivity : BaseActivity(), AnkoLogger {

    abstract val container: ViewGroup
    abstract val layoutId: Int
    abstract val defaultScreen: Screen

    private var setScreenSubscription: Subscription? = null
    private var setScreensSubscription: Subscription? = null
    private var backSubscription: Subscription? = null
    private var replaceScreenSubscription: Subscription? = null

    lateinit var dispatcher: SingleRootDispatcher

    abstract fun onScreenAppears(ongoingScreen: Screen)
    abstract fun onTopLevelScreenAppears()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        dispatcher.rootHolder.root = container
    }

    override fun onResume() {
        super.onResume()

        setScreenSubscription = RxBus.register<SetScreenEvent> {
            setScreen(it.screen)
        }

        setScreensSubscription = RxBus.register<SetScreensEvent> {
            setScreens(it.screens, it.direction)
        }

        replaceScreenSubscription = RxBus.register<ReplaceScreenEvent> {
            onHistoryChange(it.screen)
            runOnUiThread { flow.replaceHistory(it.screen, it.direction) }
        }

        backSubscription = RxBus.register<GoBackEvent> {
            if (flow.goBack().not()) {
                finish()
                return@register
            }
            onHistoryChange()
        }
    }

    override fun onPause() {
        backSubscription?.unsubscribe()
        setScreenSubscription?.unsubscribe()
        setScreensSubscription?.unsubscribe()
        replaceScreenSubscription?.unsubscribe()
        replaceScreenSubscription = null
        setScreenSubscription = null
        backSubscription = null
        setScreensSubscription = null
        super.onPause()
    }

    override fun onBackPressed() {
        val v = container[0]
        if(v is BackPressHandler && v.handleBackPress()) {
            return
        }
        RxBus.post(GoBackEvent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        dispatcher.preSaveViewState()
        super.onSaveInstanceState(outState)
    }

    override fun attachBaseContext(newBase: Context?) {
        dispatcher = Dispatcher()
        val ctx = Flow.configure(newBase!!, this)
                .dispatcher(dispatcher)
                .defaultKey(defaultScreen)
                .keyParceler(AppKeyParceler())
                .install()
        dispatcher.setBaseContext(ctx)
        super.attachBaseContext(ctx)
    }

    private fun setScreen(screen: Screen) {
        runOnUiThread {
            onHistoryChange(screen)
            flow.set(screen)
        }
    }

    private fun setScreens(screens: List<Screen>, direction: Direction) {
        runOnUiThread {
            onHistoryChange(screens.last())
            flow.setHistory(History.emptyBuilder().pushAll(screens).build(), direction)
        }
    }

    private fun onHistoryChange(screen: Screen = flow.history.peek(1)) {
        if(screen is TopLevelScreen) {
            onTopLevelScreenAppears()
        } else {
            onScreenAppears(screen)
        }
    }

}