package com.wpt.alumnihomecoming.activities

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wpt.alumnihomecoming.R

class MainActivity : AppCompatActivity() {

    var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref=getSharedPreferences("myprefs", Context.MODE_PRIVATE)
        initFragment()

    }

    private fun initFragment() {
        val fragment: Fragment
        if (pref!!.getBoolean(Constants.IS_LOGGED_IN, false)) {
            fragment = ProfileFragment()
        } else {
            fragment = LoginFragment()
        }
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, fragment)
        ft.commit()
    }
}
