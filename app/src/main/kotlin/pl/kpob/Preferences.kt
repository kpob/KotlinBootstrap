package pl.kpob

import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.defaultSharedPreferences

/**
 * Created by krzysztofpobiarzyn on 12.01.2017.
 */
abstract class Preferences {

    private lateinit var preferences: SharedPreferences

    private val editor: SharedPreferences.Editor
        get() = preferences.edit()

    fun init(ctx: Context) {
        preferences = ctx.defaultSharedPreferences
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> findPreference(name: String, default: T): T = with(preferences) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        res as T
    }

    protected fun<T> putPreference(name: String, value: T) = with(editor) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}