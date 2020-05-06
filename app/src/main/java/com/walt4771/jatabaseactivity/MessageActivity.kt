package com.walt4771.jatabaseactivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        sendSMS()
        finish()
    }

    @SuppressLint("HardwareIds")
    fun getPhoneNum(): String? {
        val tm: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return ""
        }
        return tm.line1Number
    }

    @SuppressLint("HardwareIds")
    fun sendSMS() {
        val phoneNo: String? = getPhoneNum()
        val sms: String = "Test Message by LibCrawler"
        if (isAirplaneModeOn(this)) {
            Toast.makeText(applicationContext, "메세지 전송 실패 (비행기 모드)", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNo, null, sms, null, null)
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "메세지 전송 실패",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}