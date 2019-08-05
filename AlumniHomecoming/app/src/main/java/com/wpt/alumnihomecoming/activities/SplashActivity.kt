package com.wpt.alumnihomecoming.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wpt.alumnihomecoming.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        textView.setText(R.string.app_name)
        textView2.setText("inassociation with, DAV Public School, Rajrappa Project")
        imageView.setImageResource(R.drawable.ic_splash_logo2)
        var splashThread=SplashThread()
        splashThread.start()

    }

    inner class SplashThread: Thread {

        constructor():super(){

        }

        override fun run() {
            sleep(2000)

            var intent= Intent(this@SplashActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
