package com.hacksterkrishna.a1parents.activity

import com.hacksterkrishna.a1parents.model.ServerRequest
import com.hacksterkrishna.a1parents.model.ServerResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST



/**
 * Created by hacksterkrishna on 17/1/18.
 */
interface RequestInterface{

    @POST("viewer_api/")
    fun operation(@Body request: ServerRequest): Call<ServerResponse>
}