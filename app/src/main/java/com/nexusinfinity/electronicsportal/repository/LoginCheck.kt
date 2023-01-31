package com.nexusinfinity.electronicsportal.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.nexusinfinity.electronicsportal.singletons.VolleySingleton
import org.json.JSONObject

class LoginCheck(private val context: Context) {

    companion object {
        const val TEMP_URL = "https://gifted-wetsuit-lion.cyclic.app/"
        const val ENDPOINT: String = "${TEMP_URL}api/admin/login"
        const val CHECK_ENDPOINT: String = "${TEMP_URL}api/checktoken"
    }

    interface LoginResponse {
        fun onSuccess(token: String?)
        fun onFail(reason: String?)
    }

    interface TokenCheckResponse {
        fun validationCheck(isValid: Boolean?)
        fun onError(message: String?)
    }

    fun checkLoginUsingIdAndPass(id: String, password: String, listener: LoginResponse) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, ENDPOINT, JSONObject("{ \"Id\": $id, \"password\": $password }"),
            {
                if (it.getBoolean("status")) {
                    val token: String = it.getString("token")
                    listener.onSuccess(token)
                } else {
                    listener.onFail(it.getString("message"))
                }
            },
            {
                it.localizedMessage?.let { msg ->
                    listener.onFail(msg)
                }
            }
        )

        VolleySingleton.getInstance(context = context).addToRequestQueue(jsonObjectRequest)
    }

    fun checkTokenValidity(token: String, response: TokenCheckResponse) {
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, CHECK_ENDPOINT, null,
            {
                if (it.getBoolean("status")) {
                    response.validationCheck(true)
                } else {
                    response.validationCheck(false)
                }
            },
            {
                it.localizedMessage?.let { msg ->
                    response.onError(msg)
                }

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token
                return headers;
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }
}