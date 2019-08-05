package com.hacksterkrishna.a1parents.activity

import com.hacksterkrishna.a1buses.models.JSONResponse
import com.hacksterkrishna.a1parents.model.JSONResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by hacksterkrishna on 17/1/18.
 */
interface RequestTrackerInterface{

    @GET("beingtracked/")
    fun getJSON(): Call<JSONResponse>
}