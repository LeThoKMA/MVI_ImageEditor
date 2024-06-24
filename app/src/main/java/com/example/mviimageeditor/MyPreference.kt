package com.example.mviimageeditor

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.mviimageeditor.utils.PREF_ACCESS_TOKEN

class MyPreference(context: Context) {
    private var mPref: SharedPreferences? =
        PreferenceManager.getDefaultSharedPreferences(context)
    private var mEditor: SharedPreferences.Editor? = null

    fun saveToken(token: String) {
        mEditor = mPref?.edit()
        mEditor?.putString(PREF_ACCESS_TOKEN, token)
        mEditor?.apply()
    }
}
