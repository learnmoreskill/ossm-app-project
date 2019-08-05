package com.hacksterkrishna.a1teachers.activity

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import com.hacksterkrishna.a1teachers.R
import com.mikepenz.iconics.context.IconicsContextWrapper
import kotlinx.android.synthetic.main.activity_tack_bus.*



/**
 * Created by krishna on 27/4/18.
 */
class TrackBusActivity: AppCompatActivity(){


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation= 0F
        this.title="Track bus"

        setContentView(R.layout.activity_tack_bus)


        webviewtrackbus.settings.javaScriptEnabled = true
        webviewtrackbus.loadUrl("http://202.52.240.149:8082/m/")
        webviewtrackbus.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webviewtrackbus.getSettings().setDomStorageEnabled(true)
        webviewtrackbus.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)



    }



    override fun onBackPressed() {
        super.onBackPressed()
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
