package com.hacksterkrishna.a1parents.model



/**
 * Created by hacksterkrishna on 17/1/18.
 */
class BeingTracked {
    private var sno:String?=null

    private var tracker_username:String?=null
    private var tracker_password:String?=null
    private var bus_number:String?=null


    private var latitude:String?=null
    private var longitude:String?=null

    private var busname:String?=null
    private var bustime:String?=null
    private var busspeed:String?=null
    private var busdescription:String?=null

    fun getSno(): String {
        return sno!!
    }

    fun setSno(sno:String) {
        this.sno=sno
    }

    fun setTrackerusername(tracker_username:String) {
        this.tracker_username=tracker_username
    }

    fun setTrackerpassword(tracker_password:String) {
        this.tracker_password=tracker_password
    }

    fun setBusnumber(bus_number:String) {
        this.bus_number=bus_number
    }



    fun getLatitude(): String {
        return latitude!!
    }

    fun getLongitude(): String {
        return longitude!!
    }


    fun getBusname(): String {
        return busname!!
    }

    fun getBustime(): String {
        return bustime!!
    }

    fun getBusspeed(): String {
        return busspeed!!
    }

    fun getBusdescription(): String {
        return busdescription!!
    }
}