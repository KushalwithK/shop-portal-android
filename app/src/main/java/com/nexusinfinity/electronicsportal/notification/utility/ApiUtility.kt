package com.nexusinfinity.electronicsportal.notification.utility

import com.nexusinfinity.electronicsportal.constants.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ApiUtility {
    companion object {
        @Volatile
        private var INSTANCE: ApiInterface? = null

        fun getClient(): ApiInterface = INSTANCE ?: synchronized(this) {
            val instance = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiInterface::class.java)
            INSTANCE = instance
            instance
        }
    }
}