package com.hacksterkrishna.a1parents.model

/**
 * Created by krishna on 16/4/18.
 */
class TrackerResponse{

    private val result: String? = null
    private val beingtracked: BeingTracked? = null

    private val trackbus: ArrayList<Trackbus>?= null

    fun getResult(): String {
        return result!!
    }

    fun getBeingTracked(): BeingTracked {
        return beingtracked!!
    }

    fun getTrackbus(): ArrayList<Trackbus>{
        return trackbus!!
    }

}