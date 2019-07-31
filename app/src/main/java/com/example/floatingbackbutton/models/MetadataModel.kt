package com.example.floatingbackbutton.models

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.floatingbackbutton.contracts.MetadataContract

private const val SHARED_PREFERENCES_NAME = "FloatingBackButtonSharedPreferences"
private const val TAG = "METADATA_MODEL"
class MetadataModel(var context: Context): MetadataContract.Model{

    init{
        Log.d(TAG,"MetadataModel created")
    }

    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    override fun saveInt(key: String, data: Int) {
        sharedPreferences.edit().putInt(key,data).apply()
    }
    override fun saveBoolean(key: String, data: Boolean) {
        sharedPreferences.edit().putBoolean(key,data).apply()
    }

    override fun getInt(key: String,defaultValue:Int): Int {
        return sharedPreferences.getInt(key,defaultValue)
    }

    override fun getBoolean(key: String,defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key,defaultValue)
    }

}