package com.hacksterkrishna.a1parents.model

/**
 * Created by krishna on 16/4/18.
 */
class TrackerRequest{

    private var operation:String?=null
    private var beingtracked:BeingTracked?=null

    fun setOperation(operation:String){
        this.operation=operation
    }

    fun setBeingTracked(beingtracked: BeingTracked){
        this.beingtracked=beingtracked
    }
}