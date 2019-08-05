package com.hacksterkrishna.a1parents.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hacksterkrishna.a1parents.Constants
import com.hacksterkrishna.a1parents.R
import com.hacksterkrishna.a1parents.Utils
import com.hacksterkrishna.a1parents.activity.MapsActivity
import com.hacksterkrishna.a1parents.model.Parent
import com.hacksterkrishna.a1parents.model.Student
import com.mikepenz.iconics.view.IconicsTextView
import io.fabric.sdk.android.services.common.CommonUtils.isNullOrEmpty
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.design.longSnackbar

/**
 * Created by krishna on 17/4/18.
 */

class StudentDataAdapter(private val student: ArrayList<Student>) : RecyclerView.Adapter<StudentDataAdapter.ViewHolder>() {

    private var utils = Utils()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.std_card, viewGroup, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val studentDetails = student.get(i)

        viewHolder.tv_std_name.text = student[i].sname!!.trim()
        viewHolder.tv_std_admission.text = "{cmd-book} "+student[i].sadmsnno!!.trim()
        viewHolder.itv_std_class.text = "{cmd-school} "+student[i].sclass!!+"-"+student[i].ssec!!

        if (!isNullOrEmpty(student[i].sroll)){
            viewHolder.itv_std_roll.text = "{cmd-account-circle} Roll no: "+student[i].sroll!!.trim()
        }else{ viewHolder.itv_std_roll.visibility = View.GONE }

        if (!isNullOrEmpty(student[i].dob)){
            viewHolder.itv_std_dob.text = "{cmd-account-circle} DOB: "+student[i].dob!!.trim()
        }else{ viewHolder.itv_std_dob.visibility = View.GONE }

        viewHolder?.studentDetail=studentDetails


    }

    override fun getItemCount(): Int {
        return student.size
    }

    inner class ViewHolder(itemView: View, var studentDetail:Student?=null) : RecyclerView.ViewHolder(itemView) {
        val tv_std_name: TextView = itemView.findViewById(R.id.tv_std_name)
        val tv_std_admission: IconicsTextView = itemView.findViewById(R.id.tv_std_admission)
        val itv_std_roll: IconicsTextView = itemView.findViewById(R.id.itv_std_roll)
        val itv_std_dob: IconicsTextView = itemView.findViewById(R.id.itv_std_dob)
        val itv_std_class: IconicsTextView = itemView.findViewById(R.id.itv_std_class)

        init {
            itemView.setOnClickListener{


                if (isNullOrEmpty(studentDetail?.bus_number)){
                    longSnackbar(itemView,"Bus details not found for "+studentDetail?.sname)
                    return@setOnClickListener

                }else{
                    val intent = Intent(itemView.context, MapsActivity::class.java)
                    intent.putExtra(Constants.BUSNUMBER,studentDetail?.bus_number)
                    intent.putExtra(Constants.TRACKERTYPE,studentDetail?.tracker_type)
                    itemView.context.startActivity(intent)
                }


            }
        }

    }

}