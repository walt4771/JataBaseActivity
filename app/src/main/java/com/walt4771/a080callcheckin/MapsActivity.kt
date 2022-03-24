package com.walt4771.a080callcheckin

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.walt4771.a080callcheckin.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    // 권한 체크 요청 코드 정의
    private val MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION = 100
    private val MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION = 101

    // Location API 사용을 위하여 Geofencing Client 인스턴스를 생성해야 합니다.
    private val geofencingClient: GeofencingClient by lazy { LocationServices.getGeofencingClient(this) }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var currentContact: String = ""

    var contactsList = arrayListOf<Contacts>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        checkPermission()

        val contactAdapter = MainListAdapter(this, contactsList)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "history").build()
        addContactsList(db)
        addGeofenceList(contactsList)

        val fab = findViewById<View>(R.id.fab_btn) as FloatingActionButton
        fab.setOnClickListener { view ->

            checkPermission()

            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object: CustomDialog.ButtonClickListener{
                @SuppressLint("MissingPermission")
                override fun onClicked(addName: String, addContact: String) {

                    // 기존 지오펜스 삭제하고
                    geofencingClient.removeGeofences(geofencePendingIntent).run {
                        addOnSuccessListener {
                            Log.e("GFence", "지오펜스 삭제됨")

                            mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                                // Log.e("location", location.latitude.toString() + " " + location.longitude.toString())
                                addHistoryDB(db, History(addName, addContact, location.latitude, location.longitude))
                                contactsList.clear()
                                addContactsList(db)
                                addGeofenceList(contactsList)
                                Log.e("add", contactsList.toString())
                                contactAdapter.notifyDataSetChanged()
                            }
                        }
                        // 지오펜스 목록 삭제 실패 시
                        addOnFailureListener {
                            Snackbar.make(view, "추가에 실패하였습니다(지오펜스 삭제 실패)", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                        }
                    }
                    Snackbar.make(view, "현재 위치로 등록되었습니다", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
            })
        }

        val mainList = findViewById<ListView>(R.id.mainListView)
        mainList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val clickedIndex = id
                val clickedName = contactsList[id.toInt()].name
                val clickedLocation_x = contactsList[id.toInt()].location_x
                val clickedLocation_y = contactsList[id.toInt()].location_y
                currentContact = contactsList[id.toInt()].contact

                onMainListViewClicked(clickedName, clickedLocation_x, clickedLocation_y)
            }

        // val contactAdapter = MainListAdapter(this, contactsList)
        val mainListView = findViewById<ListView>(R.id.mainListView)
        mainListView.adapter = contactAdapter

        val addgfence = findViewById<Button>(R.id.addgfence)
        addgfence.setOnClickListener { addGeofences() }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }


    fun addContactsList(db: AppDatabase) {
        val r = Runnable{
            if(db.historyDao().getAll().isEmpty()) {
                db.historyDao().insertHistory(History("일신아파트", "080-111-1111", 37.384270, 126.948570))
                db.historyDao().insertHistory(History("카페 숨", "080-222-2222", 37.390898, 126.953094))
                db.historyDao().insertHistory(History("범계 할리스", "080-333-3333", 37.391055, 126.955139))
            }

            Log.e("for", db.historyDao().getAll().toString())
            for (i in 0 until db.historyDao().getAll().size) {
                contactsList.add(i,
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

    fun addGeofenceList(contactsList: ArrayList<Contacts>) {
        for (i in 0 until contactsList.size) {
            geofenceList.add(getGeofence(
                contactsList[i].name, Pair(contactsList[i].location_x, contactsList[i].location_y)
            ))
        }
    }

    fun addHistoryDB(db: AppDatabase, history: History){
        val r = Runnable{
                db.historyDao().insertHistory(history)
        }
        val rt = Thread(r)
        rt.start()
        rt.join()
    }

    val geofenceList: MutableList<Geofence> by lazy {
        mutableListOf(
            getGeofence("Dummy Data", Pair(52.949758, 169.936587))
        )
    }

    private fun getGeofence(reqId: String, geo: Pair<Double, Double>, radius: Float = 100f): Geofence {
        return Geofence.Builder()
            .setRequestId(reqId)    // 이벤트 발생시 BroadcastReceiver에서 구분할 id
            .setCircularRegion(geo.first, geo.second, radius)    // 위치 및 반경(m)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)        // Geofence 만료 시간
            .setLoiteringDelay(10000)                            // 머물기 체크 시간
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER               // 진입 감지시
                        or Geofence.GEOFENCE_TRANSITION_EXIT     // 이탈 감지시
                        or Geofence.GEOFENCE_TRANSITION_DWELL)   // 머물기 감지시
            .build()
    }

    // Geofence 지정 및 관련 이벤트 트리거 방식을 설정하기 위해 GeofencingRequest 를 빌드합니다
    private fun getGeofencingRequest(list: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            // Geofence 이벤트는 진입시 부터 처리할 때
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(list)    // Geofence 리스트 추가
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    // Geofencing Client에 Geofence 정보 및 트리거 방식을 가지고 있는 Geofencing Request과 이벤트 발생시 처리할 Broadcast Receiver를 추가해줍니다.
    @SuppressLint("MissingPermission")
    private fun addGeofences() {
        geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(applicationContext, "add Success", Toast.LENGTH_LONG).show()
            }
            addOnFailureListener {
                Toast.makeText(applicationContext, "add Fail", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION,
            MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION -> {
                grantResults.apply {
                    if (this.isNotEmpty()) {
                        this.forEach {
                            if (it != PackageManager.PERMISSION_GRANTED) {
                                checkPermission()
                                return
                            }
                        }
                    } else {
                        checkPermission()
                    }
                }
            }
        }
    }

    private fun checkPermission() {
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessFineLocationApproved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermissionApproved = ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

                if (!backgroundLocationPermissionApproved) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        MY_PERMISSIONS_REQ_ACCESS_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQ_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
        mMap.setOnInfoWindowClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$currentContact")
            if (intent.resolveActivity(packageManager) != null) { startActivity(intent) }
        }
    }

    @SuppressLint("MissingPermission")
    fun onMainListViewClicked(locationName: String, location_x: Double, location_y: Double) {
        // 클릭한 위치
        val myLocation = LatLng(location_x, location_y)
        mMap.addMarker(MarkerOptions().position(myLocation).title(locationName))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        // 카메라 줌
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
    }
}