package com.hacksterkrishna.a1parents.model

/**
 * Created by krishna on 31/12/17.
 */
class Location{


    private var name:String?=null
    private var desc:String?=null
    private var lat:String?=null
    private var lng:String?=null

    fun getName():String{

        return name!!
    }

    fun getDesc():String{

        return desc!!
    }

    fun getLat():String{

        return lat!!
    }

    fun getLng():String{

        return lng!!
    }

    fun setName(name:String){
        this.name=name
    }

    fun setDesc(desc:String){
        this.desc=desc
    }

    fun setLat(lat:String){

        this.lat=lat
    }

    fun setLng(lng:String){

        this.lng=lng
    }

}