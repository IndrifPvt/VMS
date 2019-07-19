package com.indrif.vms.data.prefs

import android.content.Context
import android.content.SharedPreferences


object PreferenceHandler {

    val PREF_NAME = "ARROW_PREFERENCES"
    val MODE = Context.MODE_PRIVATE
    val IS_LOGGED_IN = "IS_LOGGED_IN"
    val ADMIN_ID = "ADMIN_ID"
    val PREF_KEY_LAT = "PREF_KEY_LAT"
    val PREF_KEY_LNG = "PREF_KEY_LNG"
    val PREF_KEY_ADDRESS = "PREF_KEY_ADDRESS"
    val IS_SITE_SELECTED = "IS_SITE_SELECTED"
    val SELECTED_SITE = "SELECTED_SITE"




    fun writeBoolean(context: Context, key: String, value: Boolean) {
        getEditor(context).putBoolean(key, value).commit()
    }

    fun readBoolean(context: Context, key: String,
                    defValue: Boolean): Boolean {
        return getPreferences(context).getBoolean(key, defValue)
    }

    fun writeInteger(context: Context, key: String, value: Int) {
        getEditor(context).putInt(key, value).commit()
    }

    fun readInteger(context: Context, key: String, defValue: Int): Int {
        return getPreferences(context).getInt(key, defValue)
    }

    fun writeString(context: Context, key: String, value: String) {
        getEditor(context).putString(key, value).commit()
    }

    fun readString(context: Context, key: String, defValue: String): String {
        return getPreferences(context).getString(key, defValue)
    }

    fun writeFloat(context: Context, key: String, value: Float) {
        getEditor(context).putFloat(key, value).commit()
    }

    fun readFloat(context: Context, key: String, defValue: Float): Float {
        return getPreferences(context).getFloat(key, defValue)
    }

    fun writeLong(context: Context, key: String, value: Long) {
        getEditor(context).putLong(key, value).commit()
    }

    fun readLong(context: Context, key: String, defValue: Long): Long {
        return getPreferences(context).getLong(key, defValue)
    }

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            PREF_NAME,
            MODE
        )
    }

    fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    fun clearSharePreferences(context: Context) {
        val preferences = context.getSharedPreferences(
            PREF_NAME,
            MODE
        )

        preferences.edit().clear().commit()


    }
}
