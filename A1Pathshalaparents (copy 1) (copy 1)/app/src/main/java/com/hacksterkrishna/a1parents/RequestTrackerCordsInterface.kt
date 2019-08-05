package com.hacksterkrishna.a1parents

import com.hacksterkrishna.a1parents.model.TrackerRequest
import com.hacksterkrishna.a1parents.model.TrackerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by krishna on 16/4/18.
 */
interface RequestTrackerCordsInterface{

    @POST("beingtrackedcords/")
    fun getCords(@Body request: TrackerRequest): Call<TrackerResponse>
}