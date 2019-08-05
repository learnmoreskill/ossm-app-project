package com.hacksterkrishna.a1parents.model

import java.util.*


/**
 * Created by hacksterkrishna on 17/1/18.
 */
class BeingTracked {
    private var sno:String?=null
    private var tracker_username:String?=null
    private var tracker_password:String?=null
    private var name:String?=null
    private var lastupdated_at:String?=null
    private var latitude:String?=null
    private var longitude:String?=null

    var description:String?=null
    var plateNumber:String?=null
    var timeout:String?=null
    //var device_id:Objects?=null
    var valid:String?=null
    var time:String?=null
    var timeMillisecond:String?=null
    private var speed:String?=null
    var serverTime:String?=null
    var protocol:String?=null
    var power:String?=null
    var other:String?=null
    var course:String?=null
    var altitude:String?=null
    var address:String?=null

    fun getSno(): String {
        return sno!!
    }

    fun setSno(sno:String) {
        this.sno=sno
    }
    fun setUsername(tracker_username:String) {
        this.tracker_username=tracker_username
    }

    fun setPassword(tracker_password:String) {
        this.tracker_password=tracker_password
    }


    fun getName(): String {
        return name!!
    }

    fun getLastUpdate(): String {
        return lastupdated_at!!
    }

    fun getLatitude(): String {
        return latitude!!
    }

    fun getLongitude(): String {
        return longitude!!
    }

    fun getSpeed(): String {
        return speed!!
    }
}