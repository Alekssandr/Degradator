package com.degradators.degradators.common.preferencies

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanPreference(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean = true
) : ReadWriteProperty<Any, Boolean> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)!!

}