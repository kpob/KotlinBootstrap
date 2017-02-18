package pl.kpob.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import pl.kpob.extensions.realmInstance

/**
 * Created by krzysztofpobiarzyn on 11.01.2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = realmInstance()
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

}