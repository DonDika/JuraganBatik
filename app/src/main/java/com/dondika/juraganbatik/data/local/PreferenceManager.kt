package com.dondika.juraganbatik.data.local

import android.content.Context
import android.content.SharedPreferences
import com.dondika.juraganbatik.utility.Utils

class PreferenceManager(context: Context) {

    private val prefName = "juraganbatik.pref"
    private var sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    /*
    put for save data
    */
    fun putString(key: String, value: String){
        editor.putString(key, value).apply()
    }

    fun putInt(key: String, value: Int){
        editor.putInt(key, value).apply()
    }


    /*
    get for get data
    */
    fun getString(key: String): String?{
        return sharedPreferences.getString(key, "")
    }

    fun getInt(key: String): Int{
        return sharedPreferences.getInt(key, 0)
    }


    /*
    delete data
    */
    fun clearTransactionId(){
        editor.remove(Utils.PREF_TRANSACTION_ID).apply()
    }

    fun clearUser(){
        editor.remove(Utils.PREF_IS_LOGIN).apply()
        editor.remove(Utils.PREF_ROLES).apply()
    }





}