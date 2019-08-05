package com.hacksterkrishna.a1teachers.fragment

/**
 * Created by krishna on 31/12/17.
 */
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import com.crashlytics.android.Crashlytics
import com.rengwuxian.materialedittext.MaterialEditText
import com.hacksterkrishna.a1teachers.Constants
import com.hacksterkrishna.a1teachers.R
import com.hacksterkrishna.a1teachers.RequestInterface
import com.hacksterkrishna.a1teachers.models.BroadcastMessage
import com.hacksterkrishna.a1teachers.models.ServerRequest
import com.hacksterkrishna.a1teachers.models.ServerResponse
import com.hacksterkrishna.a1teachers.models.Teacher
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.jetbrains.anko.design.longSnackbar
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BroadCastSendFragment : Fragment(), View.OnClickListener {

    private var et_brd_msg: MaterialEditText ?= null
    private var btn_brd_send: AppCompatButton?= null
    private var brd_progress: ProgressBar?= null
    private var section_progress: ProgressBar?= null
    private var pref: SharedPreferences? = null
    private var Base_url: String?=null
    private var spinner_attendance_view_class : Spinner?= null
    private var spinner_attendance_view_sec : Spinner?= null
    private var sectionapi = ArrayList<String>()

    private var selectedSec:String? = null
    private var selectedStandard:String? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    var standards = listOf<String>()
    //var sections = arrayOf("Select Section","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
    var sections = listOf<String>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Fabric.with(activity, Crashlytics())
        val view = inflater!!.inflate(R.layout.fragment_broadcast_send, container, false)

        activity.title = "Class Broadcast"
        mCompositeDisposable = CompositeDisposable()
        pref=activity.getSharedPreferences("teacherPrefs", Context.MODE_PRIVATE)
        Base_url=pref!!.getString(Constants.SCHOOL_URL,"url")

        val classlist=pref!!.getString(Constants.CLASSLIST,"class")
        standards= classlist.split(",")

        initViews(view)
        return view
    }

    private fun initViews (view: View) {
        spinner_attendance_view_class=view.findViewById(R.id.spinner_attendance_view_class)
        spinner_attendance_view_sec=view.findViewById(R.id.spinner_attendance_view_sec)
        et_brd_msg=view.findViewById(R.id.et_brd_msg)
        brd_progress=view.findViewById(R.id.brd_progress)
        section_progress=view.findViewById(R.id.section_progress)
        btn_brd_send=view.findViewById(R.id.btn_brd_send)

        spinner_attendance_view_sec!!.visibility = View.GONE

        btn_brd_send!!.setOnClickListener(this)

        //set class adapter to class to both spinner
        val attendanceViewClassAdapter = ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_dropdown_item, standards)
        spinner_attendance_view_class!!.adapter = attendanceViewClassAdapter
        spinner_attendance_view_class!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                val position = spinner_attendance_view_class!!.selectedItemPosition
                selectedStandard = standards[position]
                if (selectedStandard!=null && !selectedStandard.equals(standards[0])){

                    section_progress!!.visibility = View.VISIBLE
                    spinner_attendance_view_sec!!.visibility=View.GONE

                    getsectionfromserver(selectedStandard!!)
                }else{
                    section_progress!!.visibility = View.GONE
                    spinner_attendance_view_sec!!.visibility=View.GONE
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        //set section adapter to section of view attendance report
        spinner_attendance_view_sec!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                val position = spinner_attendance_view_sec!!.selectedItemPosition
                selectedSec = sections[position]

            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }


    }

    private fun getsectionfromserver(selectedStandard:String){

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val teacher = Teacher()
        teacher.setStandard(selectedStandard!!)
        val request = ServerRequest()
        request.setOperation(Constants.FETCHSECTION)
        request.setTeacher(teacher)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSection, this::handleErrorSection))
    }
    private fun handleResponseSection(resp: ServerResponse) {
        if (resp.getResult() == Constants.SUCCESS) {

            var sectiongetapi1=resp.getSection()

            sectionapi.clear()

            sectionapi.add("Select section")
            for(data in sectiongetapi1!!){
                sectionapi.add(data.getSection_name())
            }
            sections = sectionapi

            val attendanceViewSectionAdapter = ArrayAdapter<String>(
                    activity, android.R.layout.simple_spinner_dropdown_item, sections)
            spinner_attendance_view_sec!!.adapter = attendanceViewSectionAdapter

            section_progress!!.visibility = View.GONE
            spinner_attendance_view_sec!!.visibility=View.VISIBLE

        } else{
            section_progress!!.visibility = View.GONE
            spinner_attendance_view_sec!!.visibility=View.GONE
            longSnackbar(view, resp.getMessage())
        }
    }

    private fun handleErrorSection(error: Throwable) {

        section_progress!!.visibility = View.GONE
        spinner_attendance_view_sec!!.visibility=View.GONE
        longSnackbar(view, error.localizedMessage)
        Log.d(Constants.TAG, "failed")

    }

    companion object {

        val TITLE = "SEND BROADCAST"

        fun newInstance(): BroadCastSendFragment {

            return BroadCastSendFragment()
        }
    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.btn_brd_send -> {

                val message= et_brd_msg!!.text

                if (selectedStandard==null || selectedStandard.equals(standards[0])) {
                    longSnackbar(view!!, "Please select class")
                }else if (selectedSec==null || selectedSec.equals(sections[0])){
                    longSnackbar(view!!, "Please select section")
                }else if(message==null || message.isEmpty()){
                    longSnackbar(view!!, "Field Empty")
                }
                else if(message.length>140) {
                    longSnackbar(view!!, "Can't exceed 140 chars")
                } else {
                    SendBroadcast(message.toString(),selectedStandard.toString(),selectedSec.toString())
                }
            }

        }
    }

    fun SendBroadcast(message:String,selectedStandard:String,selectedSec:String){

        brd_progress!!.visibility=View.VISIBLE
        btn_brd_send!!.visibility=View.GONE

        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val broadcastMessage = BroadcastMessage()
        broadcastMessage.setTid(pref!!.getInt(Constants.ID,0))
        broadcastMessage.setTname(pref!!.getString(Constants.NAME,"Name"))
        broadcastMessage.setStandard(selectedStandard!!)
        broadcastMessage.setSection(selectedSec!!)
        broadcastMessage.setSchoolName(pref!!.getString(Constants.SCHOOL,"School"))
        broadcastMessage.setSchoolCode(pref!!.getString(Constants.SCHOOLCODE,"SchoolCode"))
        broadcastMessage.setText(message.trim())
        val request = ServerRequest()
        request.setOperation(Constants.SEND_BRD_OPERATION)
        request.setBroadcastMessage(broadcastMessage)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))

    }

    private fun handleResponse(resp: ServerResponse) {

        if (resp.getResult() == Constants.SUCCESS) {

            brd_progress!!.visibility = View.GONE
            btn_brd_send!!.visibility = View.VISIBLE
            et_brd_msg!!.setText("")
            longSnackbar(view!!, resp.getMessage())

        } else{

            brd_progress!!.visibility = View.GONE
            btn_brd_send!!.visibility = View.VISIBLE
            longSnackbar(view!!, resp.getMessage())


        }


    }

    private fun handleError(error: Throwable) {

        brd_progress!!.visibility = View.GONE
        btn_brd_send!!.visibility = View.VISIBLE
        Log.d(Constants.TAG, "failed")
        longSnackbar(view!!, error.localizedMessage)


    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

}