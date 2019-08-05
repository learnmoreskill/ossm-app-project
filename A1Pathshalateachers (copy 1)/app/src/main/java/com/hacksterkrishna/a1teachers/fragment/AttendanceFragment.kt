package com.hacksterkrishna.a1teachers.fragment

import android.annotation.TargetApi
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import org.jetbrains.anko.design.longSnackbar
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.facebook.shimmer.ShimmerFrameLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.hacksterkrishna.a1teachers.Constants
import com.hacksterkrishna.a1teachers.R
import com.hacksterkrishna.a1teachers.RequestInterface
import com.hacksterkrishna.a1teachers.activity.AttendanceActivity
import com.hacksterkrishna.a1teachers.activity.AttendanceEditActivity
import com.hacksterkrishna.a1teachers.activity.AttendanceViewActivity
import com.hacksterkrishna.a1teachers.models.AttendanceCheck
import com.hacksterkrishna.a1teachers.models.ServerRequest
import com.hacksterkrishna.a1teachers.models.ServerResponse
import com.hacksterkrishna.a1teachers.models.Teacher
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_attendance.*
import org.jetbrains.anko.startActivity
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by krishna on 31/12/17.
 */
class AttendanceFragment: Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private var attendance_check_progress:ProgressBar? = null
    private var attendance_start_card:CardView? = null
    private var attendance_start_placeholder_card:CardView? =null
    private var tv_attendance_info:TextView? = null
    private var btn_attendance_start:TextView? = null
    private var btn_attendance_view:TextView? = null
    private var attendance_start_shimmer:ShimmerFrameLayout? = null
    private var pref: SharedPreferences? = null
    private var Base_url: String?=null

    private var attendance_view_card:CardView? = null
    private var et_attendance_view_date : EditText?= null
    private var spinner_attendance_view_class : Spinner ?= null
    private var spinner_attendance_view_sec : Spinner?= null
    private var spinner_attendance_view_class1 : Spinner ?= null
    private var spinner_attendance_view_sec1 : Spinner?= null
    private var bt_attendance_view : AppCompatButton?= null
    private var loadingsubmitbtn : ProgressBar?= null


    private var selectedSec:String? = null
    private var selectedStandard:String? = null
    private var selectedSec1:String? = null
    private var selectedStandard1:String? = null

    private var sectionapi = ArrayList<String>()
    private var sectionapi2 = ArrayList<String>()

    private var teacherId:String? = null
    private var teacherName:String? = null

    private var today:String? = null
    private var mCompositeDisposable: CompositeDisposable? = null

    var standards = listOf<String>()
    //var sections = arrayOf("Select Section","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
    var sections = listOf<String>()
    var sections2 = listOf<String>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Fabric.with(activity, Crashlytics())
        val view = inflater!!.inflate(R.layout.fragment_attendance, container, false)
        activity.title = "Attendance"
        mCompositeDisposable = CompositeDisposable()
        pref=activity.getSharedPreferences("teacherPrefs", Context.MODE_PRIVATE)
        Base_url=pref!!.getString(Constants.SCHOOL_URL,"url")

        val classlist=pref!!.getString(Constants.CLASSLIST,"class")
        standards= classlist.split(",")

        initViews(view)
        return view
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initViews(view: View){
        attendance_check_progress=view.findViewById(R.id.attendance_check_progress)
        attendance_start_card=view.findViewById(R.id.attendance_start_card)
        attendance_start_placeholder_card=view.findViewById(R.id.attendance_start_placeholder_card)
        spinner_attendance_view_class1=view.findViewById(R.id.spinner_attendance_view_class1)
        spinner_attendance_view_sec1=view.findViewById(R.id.spinner_attendance_view_sec1)
        tv_attendance_info=view.findViewById(R.id.tv_attendace_info)
        btn_attendance_start=view.findViewById(R.id.btn_attendance_start)
        btn_attendance_view=view.findViewById(R.id.btn_attendance_view)
        loadingsubmitbtn=view.findViewById(R.id.loadingsubmitbtn)

        spinner_attendance_view_class=view.findViewById(R.id.spinner_attendance_view_class)
        spinner_attendance_view_sec=view.findViewById(R.id.spinner_attendance_view_sec)
        bt_attendance_view=view.findViewById(R.id.bt_attendance_view)

        attendance_start_shimmer=view.findViewById(R.id.attendance_start_shimmer_view_container)
        attendance_view_card=view.findViewById(R.id.attendance_view_card)
        et_attendance_view_date=view.findViewById(R.id.et_attendance_view_date)

        btn_attendance_start!!.visibility = View.GONE
        btn_attendance_view!!.visibility = View.GONE
        tv_attendance_info!!.visibility = View.GONE
        loadingsubmitbtn!!.visibility = View.GONE

        spinner_attendance_view_sec1!!.visibility=View.GONE

        attendance_start_placeholder_card!!.visibility = View.GONE
        attendance_start_card!!.visibility = View.VISIBLE

        //attendance_start_card!!.visibility=View.GONE
        //attendance_view_card!!.visibility=View.GONE
        //attendance_start_placeholder_card!!.visibility=View.VISIBLE
        //attendance_start_shimmer!!.startShimmerAnimation()


        //btn_attendance_start!!.visibility = View.GONE
        //tv_attendance_info!!.text = getString(R.string.attendance_taken)
        //attendance_check_progress!!.visibility=View.GONE

        //attendance_start_shimmer!!.stopShimmerAnimation()

        //attendance_view_card!!.visibility = View.VISIBLE


        //start attendance button
        btn_attendance_start!!.setOnClickListener {
            //startActivity<AttendanceActivity>()

            val intent = Intent(context, AttendanceActivity::class.java)
            intent.putExtra("standard",selectedStandard1)
            intent.putExtra("sec",selectedSec1)
            context.startActivity(intent)
        }
        //view attendance report


        btn_attendance_view!!.setOnClickListener {
            //startActivity<AttendanceEditActivity>()

            val intent = Intent(context, AttendanceEditActivity::class.java)
            intent.putExtra("standard",selectedStandard1)
            intent.putExtra("sec",selectedSec1)
            intent.putExtra("teacherId",teacherId)
            intent.putExtra("teacherName",teacherName)
            context.startActivity(intent)

        }

        et_attendance_view_date!!.setOnClickListener(this)
        et_attendance_view_date!!.showSoftInputOnFocus=false


        bt_attendance_view!!.setOnClickListener(this)
        val currentDate = Date()
        today = SimpleDateFormat("yyyy-MM-dd").format(currentDate)


        //set class adapter to class to both spinner
        val attendanceViewClassAdapter = ArrayAdapter<String>(
                activity, android.R.layout.simple_spinner_dropdown_item, standards)
        spinner_attendance_view_class!!.adapter = attendanceViewClassAdapter
        spinner_attendance_view_class1!!.adapter = attendanceViewClassAdapter

        spinner_attendance_view_class!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                val position = spinner_attendance_view_class!!.selectedItemPosition
                selectedStandard = standards[position]
                if (selectedStandard!=null && !selectedStandard.equals(standards[0])){

                    section_progress!!.visibility = View.VISIBLE
                    spinner_attendance_view_sec!!.visibility=View.GONE
                    bt_attendance_view!!.visibility = View.GONE

                    getsectionfromserver(selectedStandard!!)
                }else{
                    spinner_attendance_view_sec!!.visibility=View.GONE
                    bt_attendance_view!!.visibility = View.GONE
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        spinner_attendance_view_class1!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                val position = spinner_attendance_view_class1!!.selectedItemPosition
                selectedStandard1 = standards[position]
                if (selectedStandard1!=null && !selectedStandard1.equals(standards[0])){

                    section_progress1!!.visibility = View.VISIBLE
                    spinner_attendance_view_sec1!!.visibility=View.GONE

                    btn_attendance_start!!.visibility = View.GONE
                    btn_attendance_view!!.visibility = View.GONE
                    tv_attendance_info!!.visibility = View.GONE

                    getsectionfromserver1(selectedStandard1!!)
                }else{
                    spinner_attendance_view_sec1!!.visibility=View.GONE
                    btn_attendance_start!!.visibility = View.GONE
                    btn_attendance_view!!.visibility = View.GONE
                    tv_attendance_info!!.visibility = View.GONE
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
                if (selectedStandard!=null && !selectedStandard.equals(standards[0]) && selectedSec!=null && !selectedSec.equals(sections[0])){
                    bt_attendance_view!!.visibility = View.VISIBLE
                }else{
                    bt_attendance_view!!.visibility = View.GONE
                }
            }
            override fun onNothingSelected(arg0: AdapterView<*>) {
                // TODO Auto-generated method stub
            }
        }
        spinner_attendance_view_sec1!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        arg2: Int, arg3: Long) {
                val position = spinner_attendance_view_sec1!!.selectedItemPosition
                selectedSec1 = sections2[position]
                if (selectedStandard1!=null && !selectedStandard1.equals(standards[0]) && selectedSec1!=null && !selectedSec1.equals(sections2[0])){
                    loadingsubmitbtn!!.visibility = View.VISIBLE
                    btn_attendance_start!!.visibility = View.GONE
                    btn_attendance_view!!.visibility = View.GONE
                    tv_attendance_info!!.visibility = View.GONE
                    createLayout()
                }else{
                    btn_attendance_start!!.visibility = View.GONE
                    btn_attendance_view!!.visibility = View.GONE
                    tv_attendance_info!!.visibility = View.GONE
                }
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

            var sectiongetapi=resp.getSection()
            sectionapi.clear()
            sectionapi.add("Select section")
            for(data in sectiongetapi!!){
                sectionapi.add(data.getSection_name())
            }
            sections = sectionapi

            val attendanceViewSectionAdapter = ArrayAdapter<String>(
                    activity, android.R.layout.simple_spinner_dropdown_item, sections)
            spinner_attendance_view_sec!!.adapter = attendanceViewSectionAdapter

            section_progress!!.visibility = View.GONE
            spinner_attendance_view_sec!!.visibility=View.VISIBLE

        } else{
            section_progress1!!.visibility = View.GONE
            longSnackbar(view, resp.getMessage())
        }
    }
    private fun handleErrorSection(error: Throwable) {

        section_progress!!.visibility = View.GONE
        longSnackbar(view, error.localizedMessage)
        Log.d(Constants.TAG, "failed")

    }

    private fun getsectionfromserver1(selectedStandard:String){

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
                .subscribe(this::handleResponseSection1, this::handleErrorSection1))
    }
    private fun handleResponseSection1(resp: ServerResponse) {
        if (resp.getResult() == Constants.SUCCESS) {

            var sectiongetapi1=resp.getSection()

            sectionapi2.clear()
            sectionapi2.add("Select section")
            for(data in sectiongetapi1!!){
                sectionapi2.add(data.getSection_name())
            }
            sections2 = sectionapi2

            val attendanceViewSectionAdapter = ArrayAdapter<String>(
                    activity, android.R.layout.simple_spinner_dropdown_item, sections2)
            spinner_attendance_view_sec1!!.adapter = attendanceViewSectionAdapter

            section_progress1!!.visibility = View.GONE
            spinner_attendance_view_sec1!!.visibility=View.VISIBLE

        } else{
            section_progress1!!.visibility = View.GONE
            longSnackbar(view, resp.getMessage())
        }
    }
    private fun handleErrorSection1(error: Throwable) {

        section_progress1!!.visibility = View.GONE
        longSnackbar(view, error.localizedMessage)
        Log.d(Constants.TAG, "failed")

    }

    override fun onResume() {
        super.onResume()

        val datePicker:DatePickerDialog? = fragmentManager.findFragmentByTag("Datepickerdialog") as DatePickerDialog?
        if(datePicker!=null)
            datePicker.onDateSetListener = this

        //createLayout()
        initViews(view)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_attendance_view_date ->{
                val date: Calendar = Calendar.getInstance()
                val datePicker:DatePickerDialog = DatePickerDialog.newInstance (this, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH) )
                datePicker.isThemeDark = false
                datePicker.dismissOnPause(true)
                datePicker.setVersion(DatePickerDialog.Version.VERSION_2)
                datePicker.show(fragmentManager, "Datepickerdialog")
            }
            R.id.bt_attendance_view -> {
                if(!et_attendance_view_date!!.text.isEmpty() && (selectedStandard!=null && !selectedStandard.equals(standards[0]))  &&  (selectedSec!=null && !selectedSec.equals(sections[0]))){

                    if(et_attendance_view_date!!.text.toString().replace("-","").toInt()<=today!!.replace("-","").toInt()) {
                        startActivity<AttendanceViewActivity>("date" to et_attendance_view_date!!.text.toString(),"standard" to selectedStandard,"sec" to selectedSec)
                    } else {
                        longSnackbar(view!!, "Wrong date , can't predict future !")
                    }
                } else {
                    longSnackbar(view!!, "Fields are empty !")
                }
            }
        }
    }

    private fun createLayout(){

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val attendanceCheck = AttendanceCheck()
        attendanceCheck.setClass(selectedStandard1!!)
        attendanceCheck.setSec(selectedSec1!!)
        val request = ServerRequest()
        request.setOperation(Constants.CHECK_ATTENDANCE_DONE)
        request.setAttendanceCheck(attendanceCheck)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }

    private fun handleResponse(resp: ServerResponse) {

        if (resp.getResult() == Constants.YES) {

            if (resp.getAttendanceteacher().getId()!="0" || resp.getAttendanceteacher().getId()!=""){
                teacherId=resp.getAttendanceteacher().getId()!!
                teacherName=resp.getAttendanceteacher().getName()!!
                Log.d("teacherID:",resp.getAttendanceteacher().getId()!!)
                Log.d("teacherName:",resp.getAttendanceteacher().getName()!!)
            }else{
                Log.d("OutOfIf",resp.getAttendanceteacher().getId()!!+"-")
            }

            loadingsubmitbtn!!.visibility = View.GONE
            btn_attendance_view!!.visibility = View.VISIBLE
            btn_attendance_start!!.visibility = View.GONE
            tv_attendance_info!!.visibility = View.VISIBLE
            tv_attendance_info!!.text = getString(R.string.attendance_taken)
            //attendance_check_progress!!.visibility=View.GONE
            attendance_start_placeholder_card!!.visibility = View.GONE
            attendance_start_shimmer!!.stopShimmerAnimation()
            attendance_start_card!!.visibility = View.VISIBLE
            attendance_view_card!!.visibility = View.VISIBLE


        } else if (resp.getResult() == Constants.NO) {

            loadingsubmitbtn!!.visibility = View.GONE
            btn_attendance_view!!.visibility = View.GONE
            btn_attendance_start!!.visibility = View.VISIBLE
            tv_attendance_info!!.visibility = View.VISIBLE
            tv_attendance_info!!.text = getString(R.string.attendance_take)
            //attendance_check_progress!!.visibility=View.GONE
            attendance_start_placeholder_card!!.visibility = View.GONE
            attendance_start_shimmer!!.stopShimmerAnimation()
            attendance_start_card!!.visibility = View.VISIBLE
            attendance_view_card!!.visibility = View.VISIBLE
            //longSnackbar(view!!, resp.getMessage())


        } else {
            //attendance_check_progress!!.visibility=View.GONE
            loadingsubmitbtn!!.visibility = View.GONE
            attendance_start_placeholder_card!!.visibility = View.GONE
            attendance_start_shimmer!!.stopShimmerAnimation()
            attendance_start_card!!.visibility = View.GONE
            attendance_view_card!!.visibility = View.GONE
            longSnackbar(view!!, resp.getMessage())
        }
    }

    private fun handleError(error: Throwable) {

        //attendance_check_progress!!.visibility=View.GONE
        attendance_start_placeholder_card!!.visibility = View.GONE
        attendance_start_shimmer!!.stopShimmerAnimation()
        attendance_start_card!!.visibility = View.GONE
        attendance_view_card!!.visibility = View.GONE
        Log.d(Constants.TAG, "failed")
        longSnackbar(view!!, error.localizedMessage)

    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

        override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month:String
        val day:String
        if(monthOfYear+1<=9){
            month="0"+(monthOfYear+1).toString()
        } else {
            month=(monthOfYear+1).toString()
        }
        if(dayOfMonth<=9){
            day="0"+dayOfMonth.toString()
        } else{
            day=dayOfMonth.toString()
        }
        val newDate:String=year.toString()+"-"+month+"-"+day
        et_attendance_view_date!!.setText(newDate)
    }

}