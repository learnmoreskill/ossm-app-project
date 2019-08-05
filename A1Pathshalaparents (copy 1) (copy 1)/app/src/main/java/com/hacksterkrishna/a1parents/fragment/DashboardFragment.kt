package com.hacksterkrishna.a1parents.fragment

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.hacksterkrishna.a1parents.Constants
import com.hacksterkrishna.a1parents.R
import com.hacksterkrishna.a1parents.Utils
import io.fabric.sdk.android.Fabric
import io.fabric.sdk.android.services.common.CommonUtils.isNullOrEmpty
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {

    private var pref: SharedPreferences? = null
    //private var utils = Utils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title="Dashboard"
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Fabric.with(activity, Crashlytics())
        val fabric = Fabric.Builder(activity)
                .kits(Crashlytics())
                .debuggable(true)
                .build()
        Fabric.with(fabric)

        pref = activity.getSharedPreferences("parentPrefs", Context.MODE_PRIVATE)

        if (!isNullOrEmpty(pref!!.getString(Constants.NAME,""))){
            tv_father_name.text=pref!!.getString(Constants.NAME,"")!!.trim()
        }else{ tv_father_name.visibility = View.GONE }

        if (!isNullOrEmpty(pref!!.getString(Constants.MNAME,""))){
            tv_mother_name.text=pref!!.getString(Constants.MNAME,"")!!.trim()
        }else{ tv_mother_name.visibility = View.GONE }

        if (!isNullOrEmpty(pref!!.getString(Constants.EMAIL,""))){
            tv_db_email.text=pref!!.getString(Constants.EMAIL,"")!!.trim()
        }else{ tv_db_email.visibility = View.GONE }

        if (!isNullOrEmpty(pref!!.getString(Constants.PROFESSION,""))){
            tv_db_profession.text=pref!!.getString(Constants.PROFESSION,"")!!.trim()
        }else{ layout_profession.visibility = View.GONE }

        if (!isNullOrEmpty(pref!!.getString(Constants.NUMBER,"")) || !isNullOrEmpty(pref!!.getString(Constants.NUMBER2,""))){

            if (!isNullOrEmpty(pref!!.getString(Constants.NUMBER,""))){
                tv_db_mobile1.text=pref!!.getString(Constants.NUMBER,"")!!.trim()
            }else{ tv_db_mobile1.visibility = View.GONE }

            if (!isNullOrEmpty(pref!!.getString(Constants.NUMBER2,""))){
                tv_db_mobile2.text=pref!!.getString(Constants.NUMBER2,"")!!.trim()
            }else{ tv_db_mobile2.visibility = View.GONE }

        }else{ layout_mobile.visibility = View.GONE }

        if (!isNullOrEmpty(pref!!.getString(Constants.ADDRESS,""))){
            tv_db_address.text=pref!!.getString(Constants.ADDRESS,"")!!.trim()
        }else{ layout_address.visibility = View.GONE }

        tv_db_school.text=pref!!.getString(Constants.SCHOOLNAME,"")!!.trim()

    }

    companion object {

        fun newInstance(): DashboardFragment {

            return DashboardFragment()
        }
    }
}