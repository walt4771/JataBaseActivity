package com.walt4771.jatabaseactivity

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.room.Room
import org.jsoup.Jsoup

class MainViewModel(application: Application): AndroidViewModel(application) {
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////// DEPRECATED //////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    val db = Room.databaseBuilder(application, Appdatabase::class.java, "libData.db").build()

    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) { model }
        else { (manufacturer).toString() + " " + model }
    }

}