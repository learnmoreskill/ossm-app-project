package com.hacksterkrishna.a1parents.activity

import android.os.Bundle
import com.hacksterkrishna.a1parents.R
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.hacksterkrishna.a1parents.Constants
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.MenuItem
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hacksterkrishna.a1parents.RequestTrackerCordsInterface
import com.hacksterkrishna.a1parents.model.*
import com.mikepenz.iconics.context.IconicsContextWrapper
import retrofit2.Call
import retrofit2.Callback

class MapsActivity : FragmentActivity(), OnMapReadyCallback{

    private var pref: SharedPreferences? = null
    private var Base_url: String?=null
    private var bus_id: String?=null

    private var busNumber: String?=null
    private var trackerType: String?=null

    private var bustime: String?=null

    private var bus_data: ArrayList<Trackbus>? = null


    private var mCompositeDisposable: CompositeDisposable? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private lateinit var map: GoogleMap

    var mMap: GoogleMap?=null
    var context: Context?=null

    private var buslocation:Location?=null
    private var oldlocation: Location?=null

    var mythread=viewerThread()
    var cancel=0


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        /*supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation= 0F*/
        setTitle(getResources().getText(R.string.Track_Bus))

        pref = getSharedPreferences("parentPrefs", Context.MODE_PRIVATE)
        Base_url = pref!!.getString(Constants.SCHOOL_URL,"url")
        bus_id = pref!!.getString(Constants.PARENTID,"bus id not found") //change parent id to bus id

        context = this@MapsActivity

        busNumber = intent.getStringExtra(Constants.BUSNUMBER)
        trackerType = intent.getStringExtra(Constants.TRACKERTYPE)

        mCompositeDisposable = CompositeDisposable()



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        buslocation=Location("Start")
        mythread.start()


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
        /*val sydney = LatLng(-34.0, 151.0)
        mMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        //map.setOnMarkerClickListener(this)

        setUpMap()
    }


    fun setUpMap() {
        //check the location permission
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        // 1
        map.isMyLocationEnabled = true
        //map type
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        // 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
        // Got last known location. In some rare situations this can be null.
        // 3
        if (location != null) {
            lastLocation = location
            val currentLatLng = LatLng(location.latitude, location.longitude)

            placeMarkerOnMap(currentLatLng)

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
        }

        }

    }


    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))
        // 2
        map.addMarker(markerOptions)
    }



    inner class viewerThread:Thread{

        constructor():super(){
            oldlocation=Location("Start")
            oldlocation!!.latitude=0.0
            oldlocation!!.longitude=0.0
        }

        override fun run() {
            while (cancel!=1){
                try{
                    val retrofit = Retrofit.Builder()
                            .baseUrl(Base_url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                    val requestTrackerInterface = retrofit.create(RequestTrackerInterface::class.java)
                    var call:Call<JSONResponse> = requestTrackerInterface.getJSON()

                    val requestTrackerCordsInterface = retrofit.create(RequestTrackerCordsInterface::class.java)

                    val beingtracked= BeingTracked()
                    beingtracked.setTrackerusername(pref!!.getString(Constants.TRACKERUSERNAME,"username"))
                    beingtracked.setTrackerpassword(pref!!.getString(Constants.TRACKERKEY,"password"))
                    beingtracked.setBusnumber(busNumber!!)

                    val trackerRequest= TrackerRequest()
                    trackerRequest.setOperation(Constants.TRACKBUSLIVE)
                    trackerRequest.setBeingTracked(beingtracked)

                    val trackerResponse = requestTrackerCordsInterface.getCords(trackerRequest)

                    trackerResponse.enqueue(object : Callback<TrackerResponse> {
                        override fun onResponse(call: Call<TrackerResponse>, response: retrofit2.Response<TrackerResponse>) {

                            val resp = response.body()

                            if (resp!!.getResult() == Constants.SUCCESS) {

                                //Log.d(Constants.TAG, "tracking:"+resp.getBeingTracked().getLatitude()+"-"+resp.getBeingTracked().getLongitude())

                               // bus_data=resp.getTrackbus()

                                buslocation!!.latitude=resp.getBeingTracked().getLatitude().toDouble()
                                buslocation!!.longitude=resp.getBeingTracked().getLongitude().toDouble()

                                buslocation!!.speed=resp.getBeingTracked().getBusspeed().toFloat()
                                buslocation!!.provider=resp.getBeingTracked().getBusname()

                                bustime=resp.getBeingTracked().getBustime()

                                Log.d(Constants.TAG, "Busdetails:"+buslocation!!.latitude+"-"+buslocation!!.longitude)


                            } else if(resp.getResult() == Constants.FAILURE){

                                Log.e("getCordsThread",resp.getResult())
                            }
                        }

                        override fun onFailure(call: Call<TrackerResponse>, t: Throwable) {

                            Log.d(Constants.TAG, "failed")
                            buslocation!!.latitude=0.0
                            buslocation!!.longitude=0.0

                        }
                    })

                    runOnUiThread {
                        mMap!!.clear()
                        val pointer = LatLng(buslocation!!.latitude, buslocation!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(pointer)
                                .title("hello")
                                .snippet("Speed time")
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource(resources, R.mipmap.ic_bus))))
                        //mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pointer, 16f))
                    }

                    sleep(5000)

                } catch (ex:Exception){

                }
            }
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        Log.i("MapsActivity","Finished")
        cancel=1
        mythread.interrupt()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                //NavUtils.navigateUpFromSameTask(this)
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
    }
}