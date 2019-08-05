package com.hacksterkrishna.a1parents

import com.hacksterkrishna.a1parents.model.JSONResponse
import com.hacksterkrishna.a1parents.model.TrackerRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by hacksterkrishna on 17/1/18.
 */
interface RequestTrackerInterface{

    @POST("trackfromapi/")
    fun getJSON(@Body request: TrackerRequest): Call<JSONResponse>
}