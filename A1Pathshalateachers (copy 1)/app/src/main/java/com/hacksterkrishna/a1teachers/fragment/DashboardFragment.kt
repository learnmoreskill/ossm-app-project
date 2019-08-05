package com.hacksterkrishna.a1teachers.fragment

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.hacksterkrishna.a1teachers.Constants
import com.hacksterkrishna.a1teachers.R
import com.hacksterkrishna.a1teachers.Utils
import io.fabric.sdk.android.Fabric
import io.fabric.sdk.android.services.common.CommonUtils.isNullOrEmpty
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * Created by krishna on 31/12/17.
 */
class DashboardFragment:Fragment(){

    private var pref: SharedPreferences? = null
    private var tv_db_name: TextView? = null
    private var tv_db_email: TextView? = null
    private var tv_db_address: TextView? = null
    private var tv_db_school: TextView? = null
    private var tv_db_standard: TextView? = null

    private var layout_mobile: LinearLayout? = null
    private var tv_db_mobile1: TextView? = null
    private var tv_db_mobile2: TextView? = null
    private var layout_address: LinearLayout? = null

    private var utils = Utils()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Fabric.with(activity, Crashlytics())
        val view = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        activity.title = "Dashboard"
        initViews(view)
        return view
    }

    private fun initViews(view: View) {

        pref = activity.getSharedPreferences("teacherPrefs", Context.MODE_PRIVATE)

        tv_db_name=view.findViewById(R.id.tv_db_name)
        tv_db_email=view.findViewById(R.id.tv_db_email)
        //tv_db_standard=view.findViewById(R.id.tv_db_class)
        tv_db_school=view.findViewById(R.id.tv_db_school)
        tv_db_mobile1=view.findViewById(R.id.tv_db_mobile1)
        tv_db_mobile2=view.findViewById(R.id.tv_db_mobile2)
        layout_mobile=view.findViewById(R.id.layout_mobile)
        layout_address=view.findViewById(R.id.layout_address)

        tv_db_name!!.text=pref!!.getString(Constants.NAME,"Name")
        tv_db_email?.text=pref!!.getString(Constants.EMAIL,"")


        if (!isNullOrEmpty(pref!!.getString(Constants.MOBILE, "")) || !isNullOrEmpty(pref!!.getString(Constants.PHONE, ""))){

            if (!isNullOrEmpty(pref!!.getString(Constants.MOBILE, ""))){
                tv_db_mobile1?.text=pref!!.getString(Constants.MOBILE,"")!!.trim()
            }else{ tv_db_mobile1?.visibility = View.GONE }

            if (!isNullOrEmpty(pref!!.getString(Constants.PHONE, ""))){
                tv_db_mobile2?.text=pref!!.getString(Constants.PHONE,"")!!.trim()
            }else{ tv_db_mobile2?.visibility = View.GONE }

        }else{ layout_mobile?.visibility = View.GONE }



        if (!isNullOrEmpty(pref!!.getString(Constants.ADDRESS, ""))){
            tv_db_address?.text=pref!!.getString(Constants.ADDRESS,"")!!.trim()
        }else{ layout_address?.visibility = View.GONE }



        tv_db_school!!.text=pref!!.getString(Constants.SCHOOL,"School")

        //tv_db_standard!!.text=utils.getStandardName(pref!!.getString(Constants.STANDARD,"class"))+" - "+pref!!.getString(Constants.SEC,"sec")

    }


}