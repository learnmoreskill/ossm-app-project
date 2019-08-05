package com.hacksterkrishna.a1parents.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ProgressBar
import com.crashlytics.android.Crashlytics
import com.firebase.client.*
import com.google.firebase.iid.FirebaseInstanceId
import com.hacksterkrishna.a1parents.Constants
import com.hacksterkrishna.a1parents.R
import com.hacksterkrishna.a1parents.RequestInterface
import com.hacksterkrishna.a1parents.model.Parent
import com.hacksterkrishna.a1parents.model.ServerRequest
import com.hacksterkrishna.a1parents.model.ServerResponse
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

/**
 * Created by krishna on 31/12/17.
 */
class LoginActivity : AppCompatActivity() , AnkoLogger {

    private var pref: SharedPreferences? = null
    private var et_email: EditText? = null
    private var et_password: EditText? = null
    private var schoolname: AutoCompleteTextView? = null
    private var school_url: String?=null
    private var Base_url: String?=null
    private var progress: ProgressBar? = null

    private var mCompositeDisposable: CompositeDisposable? = null

    private var loadingschool: ProgressBar? = null

    private var mRefSchool: Firebase? = null

    private val mSchoolNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = resources.getString(R.string.login_page)

        mCompositeDisposable = CompositeDisposable()
        Fabric.with(this, Crashlytics())
        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)
                .build()
        Fabric.with(fabric)

        Firebase.setAndroidContext(applicationContext)

        println("Token:"+FirebaseInstanceId.getInstance().token)

        mRefSchool = Firebase("https://a1pathshalateachers.firebaseio.com/SchoolList")

        initViews()

        return
    }

    private fun getschlURL(schlname: String) {
        var name:Any?=null
        mRefSchool!!.addValueEventListener(object : ValueEventListener {
            @TargetApi(Build.VERSION_CODES.M)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val map = dataSnapshot.getValue(Map::class.java)

                //println("Map="+map)

                name = map[schlname]
                //println("Name="+name)

                if (name!=null) {
                    Base_url = name.toString()
                    school_url = name.toString()
                }else{
                    longSnackbar(login_layout, "Select Correct School Name From List")
                    return
                }
                val email = et_email!!.text.toString()
                val password = et_password!!.text.toString()
                //val id = et_id.text.toString()
                //val password = et_password!!.text.toString()

                //println("Email="+email+"pw"+password)

                if (Base_url==null){
                    longSnackbar(login_layout, "Failed to load,Please Try Agian!!")

                    return

                }

                //Base_url=school_url

                if (!email.isEmpty() && !password.isEmpty()) {

                    if(isValidMail(email)) {

                    progress!!.visibility = View.VISIBLE
                    btn_login.visibility = View.GONE
                    loginProcess(email, password)

                    } else {
                        longSnackbar(login_layout, "Invalid Email ID !")
                    }

                } else {

                    longSnackbar(login_layout, "Fields are empty !")
                }
            }

            override fun onCancelled(firebaseError: FirebaseError) {

            }

        })
    }

    private fun initViews() {

        pref = getSharedPreferences("parentPrefs", Context.MODE_PRIVATE)

        //btn_login = findViewById(R.id.btn_login)

        schoolname = findViewById(R.id.schoolname)

        val arrayAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_expandable_list_item_1,mSchoolNames)

        schoolname?.apply{threshold=0}

        schoolname?.apply {
            setAdapter(arrayAdapter)}

        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)

        progress = findViewById(R.id.progress)
        loadingschool = findViewById(R.id.loadingschool)

        schoolname!!.visibility = View.GONE
        loadingschool!!.visibility = View.VISIBLE

        mRefSchool!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val value = dataSnapshot.key
                mSchoolNames.add(value)
                arrayAdapter.notifyDataSetChanged()

                schoolname!!.visibility = View.VISIBLE
                loadingschool!!.visibility = View.GONE
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
            }
            override fun onCancelled(firebaseError: FirebaseError) {
            }
        })

        btn_login.setOnClickListener(View.OnClickListener {
            //validate school and set url to it
            var schlname = schoolname!!.text.toString()

            getschlURL(schlname)

        })
    }

    private fun isValidMail(email: String):Boolean{

        return email.contains("@")
    }

    private fun loginProcess(email: String, password: String){

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val parent = Parent(null,null,null,email,password,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)
        val request = ServerRequest(Constants.LOGIN_OPERATION,parent,null,null,null,null)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))

    }

    private fun handleResponse(resp: ServerResponse) {

        if (resp.result == Constants.SUCCESS) {
            val editor = pref!!.edit()
            editor.putBoolean(Constants.IS_LOGGED_IN, true)
            editor.putString(Constants.PARENTID, resp.parent?.parent_id)
            editor.putString(Constants.NAME, resp.parent?.spname)
            editor.putString(Constants.MNAME, resp.parent?.smname)
            editor.putString(Constants.EMAIL, resp.parent?.spemail)
            editor.putString(Constants.NUMBER, resp.parent?.spnumber)
            editor.putString(Constants.NUMBER2, resp.parent?.spnumber2)
            editor.putString(Constants.PROFESSION, resp.parent?.spprofession)
            editor.putString(Constants.ADDRESS, resp.parent?.sp_address)

            editor.putString(Constants.SCHOOLCODE, resp.parent?.sschoolcode)
            editor.putString(Constants.SCHOOLNAME, resp.parent?.sschool)
            editor.putString(Constants.SCHOOLADDRESS, resp.parent?.school_address)
            editor.putString(Constants.SCHOOLLOGO, resp.parent?.slogo)
            editor.putString(Constants.SCHOOLPHONE, resp.parent?.phone_no)
            editor.putString(Constants.SCHOOLPHONE2, resp.parent?.phone_no2)
            editor.putString(Constants.SCHOOLEMAIL, resp.parent?.email_id)
            editor.putString(Constants.SCHOOLFACEBOOK, resp.parent?.facebook)
            editor.putString(Constants.SCHOOLTWITTER, resp.parent?.twitter)
            editor.putString(Constants.SCHOOLINSTAGRAM, resp.parent?.instagram)
            editor.putString(Constants.SCHOOLYOUTUBE, resp.parent?.youtube)
            editor.putString(Constants.TRACKERUSERNAME, resp.parent?.tracker_username)
            editor.putString(Constants.TRACKERKEY, resp.parent?.tracker_password)



            //add to set url in SharedPreferences
            editor.putString(Constants.SCHOOL_URL,Base_url)

            editor.apply()
            info(FirebaseInstanceId.getInstance().token)
            submitFcmid(resp.parent?.parent_id)
        } else  {
            progress!!.visibility = View.INVISIBLE
            btn_login.visibility = View.VISIBLE
            longSnackbar(login_layout, resp.message!!)
        }
    }

    private fun handleError(error: Throwable) {

        progress!!.visibility = View.INVISIBLE
        btn_login.visibility = View.VISIBLE
        debug(error.localizedMessage)
        longSnackbar(login_layout, error.localizedMessage)

    }

    private fun submitFcmid(parent_id:String?) {

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val parent = Parent(parent_id,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,FirebaseInstanceId.getInstance().token,null,null)
        val request = ServerRequest(Constants.SUBMIT_FCMID_OPERATION,parent,null,null,null,null)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleSubmitResponse, this::handleSubmitError))

    }

    private fun handleSubmitResponse(resp: ServerResponse) {
        longSnackbar(login_layout, "Login Successful")
        if (resp.result == Constants.SUCCESS) {
            info("Notifications Enabled")
        } else {
            info("Failed: "+resp.message)
        }
        progress!!.visibility = View.INVISIBLE
        btn_login.visibility = View.VISIBLE
        goToProfile()
    }

    private fun handleSubmitError(error: Throwable) {
        progress!!.visibility = View.INVISIBLE
        btn_login.visibility = View.VISIBLE
        debug(error.localizedMessage)
        longSnackbar(login_layout, error.localizedMessage)
        goToProfile()
    }



    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    private fun goToProfile() {
        startActivity<ProfileActivity>()
        finish()
    }



}