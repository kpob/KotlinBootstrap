package pl.kpob

import android.app.Application
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm

/**
 * Created by krzysztofpobiarzyn on 18.02.2017.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

    }


    private fun initStetho() {
        Stetho.initialize(
                Stetho
                        .newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build()
        )
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}