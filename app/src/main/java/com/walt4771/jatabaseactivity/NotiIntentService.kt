package com.walt4771.jatabaseactivity

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import org.jsoup.Jsoup

class NotiIntentService : IntentService("NotiIntentService") {
    override fun onHandleIntent(intent: Intent?) {
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
        val table2 = table1.split("노트북실 ")[1]
        val table3 = table2.split(" 계 ")[0]
        val table4 = table3.split(" ")

        if(waitnum == table4[6]) {
            if(notitype){ // 메세지 인텐트
                val intent_message = Intent(this, MessageActivity::class.java)
                startActivity(intent_message.addFlags(FLAG_ACTIVITY_NEW_TASK));
            }
            else {
                if(Build.VERSION.SDK_INT >= 26) {
                    Noti_26Up("이제 노트북실을 이용할 수 있습니다", "5분 이내에 자리를 등록해주세요.")
                    waitnumInit()
                    cancelAlarm()
                } else {
                    Noti_26Low("이제 노트북실을 이용할 수 있습니다", "5분 이내에 자리를 등록해주세요.")
                    waitnumInit()
                    cancelAlarm()
                }
            }
        }
        else if(waitnum!! > table4[6]){
            if(Build.VERSION.SDK_INT >= 26) {
                Noti_26Up("대기 번호 오류", "올바른 대기 번호가 아닙니다")
                waitnumInit()
                cancelAlarm()
            } else {
                Noti_26Low("대기 번호 오류", "올바른 대기번호가 아닙니다")
                waitnumInit()
                cancelAlarm()
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
        val intent = Intent(applicationContext, NotiIntentService::class.java)
        val pending = PendingIntent.getService(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        // AlarmManager을 이용해서, 특정시간에 MyService 서비스가 시작되도록 하는 코드
        val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pending)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Noti_26Up(title:String, text:String) {
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
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId: Int = 1000
            notify(notificationId, builder.build())
        }
    }

    private fun Noti_26Low(title:String, text:String) {
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(text)

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