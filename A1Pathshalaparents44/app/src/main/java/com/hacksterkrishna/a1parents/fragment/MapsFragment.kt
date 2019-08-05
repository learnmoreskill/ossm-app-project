package com.hacksterkrishna.a1parents.fragment

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.hacksterkrishna.a1parents.Constants
import com.hacksterkrishna.a1parents.R
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_homework.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

import com.hacksterkrishna.a1parents.adapter.StudentDataAdapter
import com.hacksterkrishna.a1parents.model.*
import kotlinx.android.synthetic.main.fragment_maps.*


/**
 * Created by krishna on 31/12/17.
 */
class MapsFragment : Fragment(), AnkoLogger {

    private var std_data: ArrayList<Student>? = null
    private var std_adapter: StudentDataAdapter? = null
    private var mCompositeDisposable: CompositeDisposable? = null
    private var pref: SharedPreferences? = null
    private var Base_url: String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity.title="Select Student"
        return inflater.inflate(R.layout.fragment_maps, container, false)

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
        Base_url=pref!!.getString(Constants.SCHOOL_URL,"url")

        mCompositeDisposable = CompositeDisposable()
        std_card_recycler_view.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        std_card_recycler_view.layoutManager = layoutManager
        getStudent()
    }

    private fun getStudent(){

        std_progress.visibility = View.VISIBLE
        std_error_card.visibility = View.GONE
        std_card_recycler_view.visibility = View.GONE

        val requestInterface = Retrofit.Builder()
                .baseUrl(Base_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RequestInterface::class.java)

        pref = activity.getSharedPreferences("parentPrefs", Context.MODE_PRIVATE)
        val parent_id:String = pref!!.getString(Constants.PARENTID,"parent_id")

        val parent = Parent(parent_id,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)
        //request type
        val request = ServerRequest(Constants.FETCH_STUDENT,parent,null,null,null,null)
        mCompositeDisposable?.add(requestInterface.operation(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))


    }

    private fun handleResponse(resp: ServerResponse) {

        if (resp.result == Constants.SUCCESS) {
            std_data=resp.student
            std_adapter = StudentDataAdapter(std_data!!)
            std_progress.visibility = View.GONE
            std_card_recycler_view.visibility = View.VISIBLE
            std_card_recycler_view.adapter = std_adapter

        } else{

            std_progress.visibility = View.GONE
            tv_std_error_msg.text=resp.message
            std_error_card.visibility = View.VISIBLE

        }

    }

    private fun handleError(error: Throwable) {

        std_progress!!.visibility = View.GONE
        tv_hw_error_msg!!.text=error.localizedMessage
        std_error_card!!.visibility = View.VISIBLE
        debug("failed")
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.clear()
    }

    companion object {
        fun newInstance(): MapsFragment {

            return MapsFragment()
        }
    }
}