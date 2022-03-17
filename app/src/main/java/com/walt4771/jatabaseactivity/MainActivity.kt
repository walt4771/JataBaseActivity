package com.walt4771.jatabaseactivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main)
        checkAndRequestPermissions()

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
        when (sharedPreferences.getString("key_libselect", "8094")) {
            // 박달도서관(8093)
            "8093" -> { text_lib_name.text = "박달도서관" }
            // 평촌도서관(8094)
            "8094" -> { text_lib_name.text = "평촌도서관" }
            // 호계도서관(8095)
            "8095" -> { text_lib_name.text = "호계도서관" }
            // 비산도서관(8095)
            "8096" -> { text_lib_name.text = "비산도서관" }
        }


        val fab = findViewById<View>(R.id.fab_btn) as FloatingActionButton
        fab.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object: CustomDialog.ButtonClickListener{
                @SuppressLint("MissingPermission")
                override fun onClicked(text_waitnum: String) {
                    Log.e("DEBUG", "BUTTON PRESSED")
                    if (isAirplaneModeOn(applicationContext)) { Toast.makeText(applicationContext, "정보 가져오기 실패 (비행기 모드)", Toast.LENGTH_LONG).show() }
                    else {
                        if((text_waitnum).toInt() < 0) { Toast.makeText(applicationContext, "올바르지 않은 대기 번호입니다", Toast.LENGTH_LONG).show() }
                        else{
                            // Change Preference, key_waitnum
                            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                            val edit = sharedPref.edit()
                            edit.putString("key_waitnum", text_waitnum)
                            edit.apply()

                            // SetAlarm
                            val intent = Intent(applicationContext, NotiIntentService::class.java)
                            val pending = PendingIntent.getService(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                            val calendar = Calendar.getInstance()

                            // AlarmManager을 이용해서, 특정시간에 MyService 서비스가 시작되도록 하는 코드
                            val am = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            am.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                1 * 60 * 1000,
                                pending
                            )
                        }
                    }
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        when (item.itemId) {
            R.id.action_btn1 -> run {
                var preferenceIntent = Intent(this, SettingsActivity::class.java)
                startActivity(preferenceIntent)
            }
            R.id.action_btn2 -> run {
                lifecycleScope.launch(Dispatchers.IO) { viewModel.db.clearAllTables() }
            }
            else -> { return super.onOptionsItemSelected(item) }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    private fun checkAndRequestPermissions(): Boolean {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1000
        val permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        val listPermissionsNeeded: MutableList<String> = ArrayList()

        if (locationPermission != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE) }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) { listPermissionsNeeded.add(Manifest.permission.SEND_SMS) }
        if (listPermissionsNeeded.isNotEmpty()) { ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    private var time3 = 0L
    override fun onBackPressed() {
        val time1 = System.currentTimeMillis()
        val time2 = time1 - time3
        if (time2 in 0..2000) {
            // lifecycleScope.launch(Dispatchers.IO) { viewModel.db.clearAllTables() }
            finish()
        }
        else {
            time3 = time1
            Toast.makeText(applicationContext, "뒤로가기 버튼을 한번 더 누르면 종료됩니다",Toast.LENGTH_SHORT).show()
        }
    }
}

