package com.nexusinfinity.electronicsportal.notification.utility

import com.nexusinfinity.electronicsportal.constants.Constant
import com.nexusinfinity.electronicsportal.notification.model.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @Headers("Authorization: key=${Constant.SERVER_KEY}", "Content-Type: ${Constant.CONTENT_TYPE}")
    @POST("/fcm/send")
    fun sendNotification(@Body notify: PushNotification): Call<PushNotification>
}