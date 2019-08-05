package com.hacksterkrishna.a1parents.activity

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.SimpleAdapter
import com.crashlytics.android.Crashlytics
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.hacksterkrishna.a1parents.Constants
import com.hacksterkrishna.a1parents.R
import com.hacksterkrishna.a1parents.RequestInterface
import com.hacksterkrishna.a1parents.Utils
import com.hacksterkrishna.a1parents.fragment.StudentsFragment
import com.hacksterkrishna.a1parents.model.ServerRequest
import com.hacksterkrishna.a1parents.model.ServerResponse
import com.hacksterkrishna.a1parents.model.Student
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_attendance.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AttendanceActivity : AppCompatActivity(), AnkoLogger {

    private var pref: SharedPreferences? = null
    private var Base_url: String?=null

    private val absentDateList = ArrayList<HashMap<String,String>>()
    private val KEY_DATE = "date"
    private val utils = Utils()
    private var stdid: String?=null
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        Fabric.with(this, Crashlytics())
        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)
                .build()
        Fabric.with(fabric)

        pref = this.getSharedPreferences("parentPrefs", Context.MODE_PRIVATE)
        Base_url=pref!!.getString(Constants.SCHOOL_URL,"url")

        stdid = intent.getStringExtra(Constants.STDID)

        mCompositeDisposable = CompositeDisposable()
        setAttendanceList()
    }

    private fun setAttendanceList(){

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)


        val student = Student(stdid,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)
        val request = ServerRequest(Constants.FETCH_ATTENDANCE,null,null,null,null,student)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
        attendance_progress.visibility= View.VISIBLE
    }

    private fun handleResponse(resp: ServerResponse) {

        if (resp.result == Constants.SUCCESS) {

            val attendance=resp.attendance
            val acount=resp.acount
            val pcount=resp.pcount
            for(date in attendance!!){
                val map = HashMap<String,String>()
                map.put(KEY_DATE,"{cmd-calendar} "+utils.prettifyDate(date.aclock!!))
                absentDateList.add(map)
            }

            loadListView(acount,pcount)

        } else{
            attendance_progress.visibility=View.GONE
            attendance_list_view.visibility=View.GONE
            tv_attendance_error_msg.text=resp.message
            attendance_error_card.visibility=View.VISIBLE
            debug( resp.message)


        }


    }

    private fun handleError(error: Throwable) {
        attendance_progress.visibility=View.GONE
        attendance_list_view.visibility=View.GONE
        tv_attendance_error_msg.text=error.localizedMessage
        attendance_error_card.visibility=View.VISIBLE
        debug(error.localizedMessage)

    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    private fun loadListView(acount:Int?,pcount:Int?){
        attendance_progress.visibility=View.GONE
        val total:Int=acount!! + pcount!!
        val percentage = (pcount.toFloat()/total.toFloat())*100
        val absentPercent = (acount.toFloat()/total.toFloat())*100
        var entries = ArrayList<PieEntry>()
        entries.add(PieEntry(percentage, "Present"))
        entries.add(PieEntry(absentPercent, "Absent"))
        val set = PieDataSet(entries, "Attendance")
        set.setColors((intArrayOf(R.color.colorPresent,R.color.colorAbsent)),this)
        val data = PieData(set)
        var l = attendance_piechart.legend
        l.isEnabled=true
        l.form = Legend.LegendForm.CIRCLE
        attendance_piechart.description.isEnabled=false
        attendance_piechart.centerText="Attendance"
        attendance_piechart.data = data
        attendance_piechart.invalidate()
        attendance_class_count.text=attendance_class_count.text.toString()+total.toString()
        attendance_absent_count.text=attendance_absent_count.text.toString()+acount.toString()
        attendance_present_count.text=attendance_present_count.text.toString()+pcount.toString()
        val adapter = SimpleAdapter(this, absentDateList, R.layout.attendance_list_item,
                arrayOf(KEY_DATE),
                intArrayOf(R.id.tv_absent_date))
        attendance_list_view.adapter=adapter
        setListViewHeightBasedOnChildren(attendance_list_view)
        attendance_edit_details_card.visibility=View.VISIBLE
        attendance_scrollview.postDelayed({
            attendance_scrollview.fullScroll(ScrollView.FOCUS_UP)
        },250)
    }


    /**** Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView   */
    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return

        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED)
        var totalHeight = 0
        var view: View? = null
        for (i in 0 until listAdapter.count) {
            view = listAdapter.getView(i, view, listView)
            if (i == 0)
                view!!.layoutParams = ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT)

            view!!.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += view.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }

    companion object {

        fun newInstance(): StudentsFragment {

            return StudentsFragment()
        }
    }
}
