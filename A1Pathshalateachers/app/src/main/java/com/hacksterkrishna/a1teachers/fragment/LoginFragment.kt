package com.hacksterkrishna.a1teachers.fragment

import android.annotation.TargetApi
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import org.jetbrains.anko.design.longSnackbar
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.firebase.client.*
import com.google.firebase.FirebaseApp
import com.hacksterkrishna.a1teachers.Constants
import com.hacksterkrishna.a1teachers.R
import com.hacksterkrishna.a1teachers.RequestInterface
import com.hacksterkrishna.a1teachers.activity.LoginActivity
import com.hacksterkrishna.a1teachers.models.ServerRequest
import com.hacksterkrishna.a1teachers.models.ServerResponse
import com.hacksterkrishna.a1teachers.models.Teacher
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import java.util.ArrayList
import kotlin.concurrent.thread

/**
 * Created by krishna on 31/12/17.
 */

class LoginFragment: Fragment(), View.OnClickListener{

    private var btn_login: AppCompatButton? = null
    private var schoolname: AutoCompleteTextView? = null
    private var et_email: EditText? = null
    private var et_password: EditText? = null
    private var progress: ProgressBar? = null

    private var pref: SharedPreferences? = null
    private var mCompositeDisposable: CompositeDisposable? = null

    private var school_url: String?=null
    private var Base_url: String?=null

    private val classlistname = ArrayList<Any>()


    private var loadingschool: ProgressBar? = null

    private var mRefSchool: Firebase? = null

    private val mSchoolNames = ArrayList<String>()

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Fabric.with(activity, Crashlytics())
        val view = inflater!!.inflate(R.layout.fragment_login, container, false)
        activity.title = resources.getString(R.string.login_page)
        mCompositeDisposable = CompositeDisposable()

        Firebase.setAndroidContext(context)

        mRefSchool = Firebase("https://a1pathshalateachers.firebaseio.com/SchoolList")

        initViews(view)
        return view


    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initViews(view: View) {

        pref = activity.getSharedPreferences("teacherPrefs", Context.MODE_PRIVATE)

        btn_login = view.findViewById(R.id.btn_login)

        schoolname = view.findViewById(R.id.schoolname)

        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1,mSchoolNames)


        schoolname?.apply{threshold=0}


        schoolname?.apply {
            setAdapter(arrayAdapter)}


        et_email = view.findViewById(R.id.et_email)
        et_password = view.findViewById(R.id.et_password)

        progress = view.findViewById(R.id.progress)
        loadingschool = view.findViewById(R.id.loadingschool)

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

        btn_login!!.setOnClickListener(this)
    }

    private fun getschlURL(schlname: String) {
        var name:Any?=null
        mRefSchool!!.addValueEventListener(object : ValueEventListener {
            @TargetApi(Build.VERSION_CODES.M)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val map = dataSnapshot.getValue(Map::class.java)

                //println("Map="+map)

                name = map[schlname]
                println("Name="+name)

                if (name!=null) {
                    Base_url = name.toString()
                    school_url = name.toString()
                }else{
                    Snackbar.make(view!!, "Select Correct School Name From List", Snackbar.LENGTH_LONG).show()
                    return
                }
                val email = et_email!!.text.toString()
                val password = et_password!!.text.toString()
                if (Base_url==null){
                    Snackbar.make(view!!, "Failed to load,Please Try Agian!!", Snackbar.LENGTH_LONG).show()

                    return

                }

                //Base_url=school_url

                if (!email.isEmpty() && !password.isEmpty()) {

                    if(isValidMail(email)) {

                        progress!!.visibility = View.VISIBLE
                        btn_login!!.visibility = View.GONE
                        loginProcess(email, password)
                    } else {
                        longSnackbar(view!!, "Invalid Email ID !")
                    }

                } else {

                    longSnackbar(view!!, "Fields are empty !")
                }
            }

            override fun onCancelled(firebaseError: FirebaseError) {

            }

        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_login -> {

                //validate school and set url to it
                var schlname = schoolname!!.text.toString()

                getschlURL(schlname)

            }
        }
    }

    private fun isValidMail(email: String):Boolean{

        return email.contains("@")
    }

    private fun loginProcess(email: String, password: String) {

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        val teacher = Teacher()
        teacher.setEmail(email)
        teacher.setPassword(password)
        val request = ServerRequest()
        request.setOperation(Constants.LOGIN_OPERATION)
        request.setTeacher(teacher)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))

    }

    private fun handleResponse(resp: ServerResponse) {

        longSnackbar(view!!, resp.getMessage())


        if (resp.getResult() == Constants.SUCCESS) {

           var classlist=resp.getClasslist()

            //var sectionlist=resp.getClasssection().toString()
            //var sectionlistnew = sectionlist.replace("=",":")











            val editor = pref!!.edit()
            editor.putBoolean(Constants.IS_LOGGED_IN, true)

             val sb = StringBuilder()
             var j=0
             sb.append("Select Class").append(",")
             for (i in classlist){
                 sb.append(classlist[j!!].getClassname()).append(",")
                 j++
             }
             var sbn=sb.toString()
             sbn = sbn.substring(0, sbn.length - 1)
             editor.putString(Constants.CLASSLIST,sbn)


            /*val sbsection = StringBuilder()
            var k=0
            //sb.append("Select Section").append(",")
            for (i in sectionlist){
                val value = i
                classlistname.add(value)
                sbsection.append(classsection[k!!]).append(",")
                k++
            }
            var sbsectionnew=sbsection.toString()
            sbsectionnew = sbsectionnew.substring(0, sbsectionnew.length - 1)*/



            //editor.putString(Constants.SECTIONLIST,sectionlistnew)



            editor.putInt(Constants.ID, resp.getTeacher().getId())
            editor.putString(Constants.NAME, resp.getTeacher().getName())
            editor.putString(Constants.EMAIL, resp.getTeacher().getEmail())
            editor.putString(Constants.ADDRESS, resp.getTeacher().getAddress())
            editor.putString(Constants.MOBILE, resp.getTeacher().getMobile()!!)
            editor.putString(Constants.PHONE, resp.getTeacher().getPhone())

            editor.putString(Constants.SEX, resp.getTeacher().getSex()!!)
            editor.putString(Constants.DOB, resp.getTeacher().getDob())
            editor.putString(Constants.FATHER, resp.getTeacher().getFather())
            editor.putString(Constants.MOTHER, resp.getTeacher().getMother())
            editor.putString(Constants.COUNTRY, resp.getTeacher().getCountry())
            editor.putString(Constants.MARITAL, resp.getTeacher().getMarital())
            editor.putString(Constants.IDPROOF, resp.getTeacher().getIdproof())
            editor.putString(Constants.DOC2, resp.getTeacher().getDoc2())
            editor.putString(Constants.DOC3, resp.getTeacher().getDoc3())
            editor.putString(Constants.JOINDATE, resp.getTeacher().getJoindate())
            editor.putString(Constants.IMAGE, resp.getTeacher().getImage())
            editor.putString(Constants.SALARY, resp.getTeacher().getSalary())
            editor.putString(Constants.JOBTYPE, resp.getTeacher().getJobtype())

            editor.putString(Constants.SCHOOL, resp.getTeacher().getSchool())
            editor.putString(Constants.SCHOOLCODE, resp.getTeacher().getSchoolcode())

            editor.putString(Constants.SCHOOLADDRESS, resp.getTeacher().getSchool_address())
            editor.putString(Constants.SCHOOLLOGO, resp.getTeacher().getSlogo())
            editor.putString(Constants.SCHOOLPHONE, resp.getTeacher().getPhone_no())
            editor.putString(Constants.SCHOOLPHONE2, resp.getTeacher().getPhone_no2())
            editor.putString(Constants.SCHOOLEMAIL, resp.getTeacher().getEmail_id())
            editor.putString(Constants.SCHOOLFACEBOOK, resp.getTeacher().getFacebook())
            editor.putString(Constants.SCHOOLTWITTER, resp.getTeacher().getTwitter())
            editor.putString(Constants.SCHOOLINSTAGRAM, resp.getTeacher().getInstagram())
            editor.putString(Constants.SCHOOLYOUTUBE, resp.getTeacher().getYoutube())
            editor.putString(Constants.TRACKERUSERNAME, resp.getTeacher().getTracker_username())
            editor.putString(Constants.TRACKERKEY, resp.getTeacher().getTracker_password())
            editor.putString(Constants.SMSTOKEN, resp.getTeacher().getSms_token())

            editor.putString(Constants.STANDARD, resp.getTeacher().getStandard())
            editor.putString(Constants.SEC, resp.getTeacher().getSec())

            //add to set url in SharedPreferences
            editor.putString(Constants.SCHOOL_URL,Base_url)

            editor.apply()



            goToProfile()

        }
        progress!!.visibility = View.INVISIBLE
        btn_login!!.visibility = View.VISIBLE

    }

    private fun handleError(error: Throwable) {

        progress!!.visibility = View.INVISIBLE
        btn_login!!.visibility = View.VISIBLE
        Log.d(Constants.TAG, "failed"+error.localizedMessage)
        longSnackbar(view!!, error.localizedMessage)

    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    private fun goToProfile() {
        startActivity<LoginActivity>()
        activity.finish()
    }

}