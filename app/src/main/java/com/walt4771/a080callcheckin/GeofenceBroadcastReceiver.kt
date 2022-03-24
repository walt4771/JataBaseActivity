package com.walt4771.a080callcheckin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent2 = Intent(context, NotificationClickedActivity::class.java)
        fun createNotification(title: String, text: String){
            val pendingIntent:PendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
                }

            val builder = context?.let { it1 ->
                NotificationCompat.Builder(it1, "MY_channel")
                    .setSmallIcon(R.mipmap.ic_launcher_main)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            }

            // 오레오 버전 이후에는 알림을 받을 때 채널이 필요
            val channel_id = "MY_channel" // 알림을 받을 채널 id 설정
            val channel_name = "채널이름" // 채널 이름 설정
            val descriptionText = "설명글" // 채널 설명글 설정
            val importance = NotificationManager.IMPORTANCE_DEFAULT // 알림 우선순위 설정
            val channel = NotificationChannel(channel_id, channel_name, importance).apply {
                description = descriptionText
            }

            // 만든 채널 정보를 시스템에 등록
            // val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager
            val notificationManager = context?.getSystemService(NotificationManager::class.java) as NotificationManager

            notificationManager.createNotificationChannel(channel)

            // 알림 표시: 알림의 고유 ID(ex: 1002), 알림 결과
            if (builder != null) { notificationManager.notify(1002, builder.build()) }
        }

        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e("GeofenceBR", errorMessage)
                return
            }
        }

        // get transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get triggered geofences
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val transitionMsg = when(geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Enter"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Exit"
                else -> "-"
            }
            triggeringGeofences.forEach {
                if (transitionMsg == "Enter") {
                    intent2.putExtra("currentName", it.requestId)
                    createNotification(it.requestId + "에 방문 예정이신가요?", "알림을 클릭하여 콜 체크인하세요")
                }
                Toast.makeText(context, "${it.requestId} - $transitionMsg", Toast.LENGTH_LONG).show()
            }
        } else { Toast.makeText(context, "알 수 없음", Toast.LENGTH_LONG).show() }
    }
}