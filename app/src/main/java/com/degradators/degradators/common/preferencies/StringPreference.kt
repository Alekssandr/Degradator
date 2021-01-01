package com.degradators.degradators.common.preferencies

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPreference(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String = ""
) : ReadWriteProperty<Any, String> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) =
        sharedPreferences.edit()
            .putString(key, value)
            .apply()

    override fun getValue(thisRef: Any, property: KProperty<*>): String =
        sharedPreferences.getString(key, defaultValue)!!

}