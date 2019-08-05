package com.hacksterkrishna.a1parents.model

/**
 * Created by hacksterkrishna on 17/1/18.
 */
class JSONResponse{
    private var beingtracked:ArrayList<BeingTracked>?=null

            fun getBeingTracked():ArrayList<BeingTracked>{
        return beingtracked!!
    }

}