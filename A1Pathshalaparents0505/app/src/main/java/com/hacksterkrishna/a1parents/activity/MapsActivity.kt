package com.hacksterkrishna.a1parents.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.hacksterkrishna.a1parents.adapter.HomeworkDataAdapter
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_homework.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.PlaceReport
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.hacksterkrishna.a1parents.*
import com.hacksterkrishna.a1parents.R
import com.hacksterkrishna.a1parents.model.*
import com.hacksterkrishna.a1parents.networkoperations.GooglePlacesApi
import com.mikepenz.iconics.context.IconicsContextWrapper
import org.jetbrains.anko.runOnUiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

class MapsActivity : FragmentActivity(), OnMapReadyCallback{

    private var pref: SharedPreferences? = null
    private var Base_url: String?=null
    private var bus_id: String?=null

    private var busNumber: String?=null
    private var trackerType: String?=null

    private var busName: String?=null

    private var bustime: String?=null

    private var data: ArrayList<BeingTracked>? = null


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


                    val tracker_username=pref!!.getString(Constants.TRACKERUSERNAME,"username")
                    val tracker_password = pref!!.getString(Constants.TRACKERKEY,"password")

                    val requestTrackerInterface = retrofit.create(RequestTrackerInterface::class.java)

                    val beingtracked = BeingTracked()
                    beingtracked.setUsername(tracker_username!!)
                    beingtracked.setPassword(tracker_password!!)
                    val trackerRequest= TrackerRequest()
                    trackerRequest.setOperation(Constants.TRACKBUSLIVE)
                    trackerRequest.setBeingTracked(beingtracked)

                    var call:Call<JSONResponse> = requestTrackerInterface.getJSON(trackerRequest)

                    call.enqueue(object : Callback<JSONResponse> {

                        override fun onResponse(call: Call<JSONResponse>, response: Response<JSONResponse>) {

                            val jsonResponse:JSONResponse = response.body()!!
                            data = jsonResponse.getBeingTracked()
                            for (i in data!!){
                                i.getName()
                                if (i.getName()==busNumber) {
                                    buslocation!!.latitude=i.getLatitude().toDouble()
                                    buslocation!!.longitude=i.getLongitude().toDouble()
                                    buslocation!!.provider=i.getName()
                                    buslocation!!.speed=i.getSpeed().toFloat()

                                    Log.d("Busname", i.getName())
                                }

                            }

                        }

                        override fun onFailure(call: Call<JSONResponse>, t: Throwable) {
                            Log.d("Error", t.message)

                            buslocation!!.latitude=0.0
                            buslocation!!.longitude=0.0
                        }
                    })


                    runOnUiThread {
                        mMap!!.clear()
                        val pointer = LatLng(buslocation!!.latitude, buslocation!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(pointer)
                                .title(buslocation!!.provider)
                                .snippet("Speed:"+buslocation!!.speed.toString())
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        BitmapFactory.decodeResource(resources, R.mipmap.ic_bus))))
                        //mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pointer, 16f))
                    }

                    sleep(15000)

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