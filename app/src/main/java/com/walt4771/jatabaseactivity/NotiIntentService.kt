package com.walt4771.jatabaseactivity

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import org.jsoup.Jsoup

class NotiIntentService : IntentService("NotiIntentService") {
    var newLib:LibData = LibData()

    override fun onHandleIntent(intent: Intent?) {
        Log.i("DEBUG", "ThreadStarted")
        var url = "http://222.233.168.6:"

        // 설정 가져오기
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        val selectedlib = sharedPref.getString("key_libselect", "8094")
        val waitnum = sharedPref.getString("key_waitnum", "100")
        val notitype:Boolean = sharedPref.getBoolean("key_notitype", false)

        // URL Connect
        if(url.takeLast(1) == ":") { url += "$selectedlib" } // 연속눌림 방지
        val d = Jsoup.connect(url).get()
        val table1 = (d.select("table")[1].text()).toString()

        when (selectedlib) {
            // 박달도서관(8093)
            "8093" -> {
                // 문자열 처리와 띄어쓰기 예외처리, 44, 72, 103
                val table2 = StringBuilder(table1)
                table2.delete(0, 41)

                val args = listOf("0", "2", "30", "57")
                for (i in args) {
                    table2.deleteCharAt((i.toInt()))
                }
                val table3 = String(table2)

                // 객체를 따로 생성해 값 직접 대입
                fun notebookData(str: String): LibData {
                    val str1 = str.split(" ")
                    val i = 16
                    newLib.tableid = str1[i]
                    newLib.All = str1[i + 1]
                    newLib.using = str1[i + 2]
                    newLib.remaining = str1[i + 3]
                    newLib.usage = str1[i + 4]
                    newLib.waiting = str1[i + 5]
                    newLib.calling = str1[i + 6]
                    newLib.scheduled = str1[i + 7]
                    return newLib
                }
                newLib = notebookData(table3)
            }

            // 평촌도서관(8094)
            "8094" -> {
                // 문자열 처리와 띄어쓰기 예외처리, 44, 72, 103
                val table2 = StringBuilder(table1)
                table2.delete(0, 41)

                val args = listOf("0", "2", "30", "58", "88", "118", "143")
                for (i in args) {
                    table2.deleteCharAt((i.toInt()))
                }
                val table3 = String(table2)

                // 객체를 따로 생성해 값 직접 대입
                fun notebookData(str: String): LibData {
                    val str1 = str.split(" ")
                    val newLib = LibData()
                    val i = 40
                    newLib.tableid = str1[i]
                    newLib.All = str1[i + 1]
                    newLib.using = str1[i + 2]
                    newLib.remaining = str1[i + 3]
                    newLib.usage = str1[i + 4]
                    newLib.waiting = str1[i + 5]
                    newLib.calling = str1[i + 6]
                    newLib.scheduled = str1[i + 7]
                    return newLib
                }
                newLib = notebookData(table3)
            }

            // 호계도서관(8095)
            "8095" -> {
                // 문자열 처리와 띄어쓰기 예외처리, 44, 72, 103
                val table2 = StringBuilder(table1)
                table2.delete(0, 41)

                val args = listOf("0", "2", "4", "31", "60", "89", "91", "116")
                for (i in args) {
                    table2.deleteCharAt((i.toInt()))
                }
                val table3 = String(table2)

                // 객체를 따로 생성해 값 직접 대입
                fun notebookData(str: String): LibData {
                    val str1 = str.split(" ")
                    val newLib = LibData()
                    val i = 32
                    newLib.tableid = str1[i]
                    newLib.All = str1[i + 1]
                    newLib.using = str1[i + 2]
                    newLib.remaining = str1[i + 3]
                    newLib.usage = str1[i + 4]
                    newLib.waiting = str1[i + 5]
                    newLib.calling = str1[i + 6]
                    newLib.scheduled = str1[i + 7]
                    return newLib
                }
                newLib = notebookData(table3)
            }

            // 비산도서관(8095)
            "8096" -> {
                // 문자열 처리와 띄어쓰기 예외처리, 44, 72, 103
                val table2 = StringBuilder(table1)
                table2.delete(0, 41)

                val args = listOf("0", "2", "29", "55")
                for (i in args) {  table2.deleteCharAt((i.toInt())) }
                val table3 = String(table2)

                // 객체를 따로 생성해 값 직접 대입
                fun notebookData(str: String): LibData {
                    val str1 = str.split(" ")
                    val newLib = LibData()
                    val i = 16
                    newLib.tableid = str1[i]
                    newLib.All = str1[i + 1]
                    newLib.using = str1[i + 2]
                    newLib.remaining = str1[i + 3]
                    newLib.usage = str1[i + 4]
                    newLib.waiting = str1[i + 5]
                    newLib.calling = str1[i + 6]
                    newLib.scheduled = str1[i + 7]
                    return newLib
                }
                newLib = notebookData(table3)
            }
        }

        if(waitnum == newLib.calling) {
            if(notitype){ // 메세지 인텐트
                val intent_message = Intent(this, MessageActivity::class.java)
                startActivity(intent_message.addFlags(FLAG_ACTIVITY_NEW_TASK));
            }
            else {
                if(Build.VERSION.SDK_INT >= 26) {
                    Noti_26Up()
                    waitnumInit()
                    cancelAlarm()
                } else {
                    Noti_26Low()
                    waitnumInit()
                    cancelAlarm()
                }
            }
        }
    }

    private fun waitnumInit(){
        // Change Preference, key_waitnum
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        val edit = sharedPref.edit()
        edit.putString("key_waitnum", null)
        edit.apply()
    }

    private fun cancelAlarm() {
        // SetAlarm
        val intent = Intent(applicationContext, NotiIntentService::class.java)
        val pending = PendingIntent.getService(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // AlarmManager을 이용해서, 특정시간에 MyService 서비스가 시작되도록 하는 코드
        val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Noti_26Up() {
        // Notification Channel
        val CHANNEL_ID = "Important Notification"
        val name = "channel01"
        val descriptionText = "channel_for_MM"

        // Create the NotificationChannel
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText

        // Register Channel
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setContentTitle(newLib.tableid + "의 차례가 왔습니다!")
            .setContentText("5분 내에 자리를 등록해주세요. (호출번호: " + newLib.Calling + ")")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId: Int = 1000
            notify(notificationId, builder.build())
        }
    }

    private fun Noti_26Low() {
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(newLib.tableid + "의 차례가 왔습니다!")
            .setContentText("5분 내에 자리를 등록해주세요. (호출번호: " + newLib.Calling + ")")

        val resultIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL

        val notificationManager =
            NotificationManagerCompat.from(this)
        notificationManager.notify(0, notification)
    }
}