package com.walt4771.a080callcheckin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room

class NotificationClickedActivity : AppCompatActivity() {
    var contactsListBR = arrayListOf<Contacts>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_clicked)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "history").build()
        addContactsList(db)

        for(i in 0 until contactsListBR.size){
            if( intent.getStringExtra("currentName") == contactsListBR[i].name){
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + contactsListBR[i].contact)
                if (intent.resolveActivity(packageManager) != null) { startActivity(intent) }
                // finish()
            }
        }

    }

    private fun addContactsList(db: AppDatabase) {
        val r = Runnable{
            Log.e("for", db.historyDao().getAll().toString())
            for (i in 0 until db.historyDao().getAll().size) {
                contactsListBR.add(i,
                    Contacts(
                        db.historyDao().getAll()[i].name,
                        db.historyDao().getAll()[i].contact.toString(),
                        db.historyDao().getAll()[i].location_x,
                        db.historyDao().getAll()[i].location_y
                    )
                )
            }
        }
        val rt = Thread(r)
        rt.start()
        rt.join()
    }
}