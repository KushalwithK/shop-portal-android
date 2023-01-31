package com.nexusinfinity.electronicsportal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nexusinfinity.electronicsportal.databinding.ActivityLoginBinding
import com.nexusinfinity.electronicsportal.repository.LoginCheck

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginCheck: LoginCheck = LoginCheck(this)
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar = binding.progressBar
        progressBar.visibility = View.GONE
        binding.adminIDInput.isEnabled = true
        binding.adminPASSInput.isEnabled = true
        binding.authenticateButton.isEnabled = true

        loginIfPossible()

        binding.adminIDInput.requestFocus()

        binding.authenticateButton.setOnClickListener {

            val adminInput = binding.adminIDInput.editableText
            val adminPassword = binding.adminPASSInput.editableText
            if (adminInput.isNotEmpty() && adminPassword.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                binding.adminIDInput.isEnabled = false
                binding.adminPASSInput.isEnabled = false
                binding.authenticateButton.isEnabled = false

                loginCheck.checkLoginUsingIdAndPass(
                    adminInput.toString(),
                    adminPassword.toString(),
                    object : LoginCheck.LoginResponse {
                        override fun onSuccess(token: String?) {
                            token?.let {
                                Toast.makeText(this@LoginActivity, "Admin token created, valid for another 24hrs.", Toast.LENGTH_SHORT).show()
                                loginSuccess(adminInput.toString(), adminPassword.toString(), token)
                            }
                        }

                        override fun onFail(reason: String?) {
                            reason?.let {
                                Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                                progressBar.visibility = View.GONE
                                binding.adminIDInput.isEnabled = true
                                binding.adminPASSInput.isEnabled = true
                                binding.authenticateButton.isEnabled = true
                                binding.adminIDInput.error = it
                                binding.adminIDInput.error
                                binding.adminPASSInput.error = it
                                binding.adminPASSInput.error
                                binding.adminIDInput.requestFocus()
                            }

                        }

                    })
            }
            if (adminInput.isEmpty()) {
                binding.adminIDInput.error = "Admin ID cannot be empty!"
                binding.adminIDInput.error
            }
            if (adminPassword.isEmpty()) {
                binding.adminPASSInput.error = "Admin PASS cannot be empty!"
                binding.adminPASSInput.error
            }
        }
    }

    private fun loginIfPossible() {
        val loginShared = getSharedPreferences("isLogin", MODE_PRIVATE)
        val login = loginShared.getBoolean("loggedIn", false)

        // Setting id, pass inputs to its original values!
        binding.adminIDInput.setText(loginShared.getString("id", null))
        binding.adminPASSInput.setText(loginShared.getString("pass", null))

        if (login) {

            progressBar.visibility = View.VISIBLE
            binding.adminIDInput.isEnabled = false
            binding.adminPASSInput.isEnabled = false
            binding.authenticateButton.isEnabled = false
            val token = loginShared.getString("token", null)

            token?.let { tok ->
                loginCheck.checkTokenValidity(tok, object : LoginCheck.TokenCheckResponse {
                    override fun validationCheck(isValid: Boolean?) {
                        isValid?.let {
                            if(it) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Admin authorized, logged in successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar.visibility = View.GONE
                                binding.adminIDInput.isEnabled = true
                                binding.adminPASSInput.isEnabled = true
                                binding.authenticateButton.isEnabled = true
                                startMain()
                            }
                            else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Admin Token expired, please login again!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar.visibility = View.GONE
                                binding.adminIDInput.isEnabled = true
                                binding.adminPASSInput.isEnabled = true
                                binding.authenticateButton.isEnabled = true
                                val sharedEdit = loginShared.edit()
                                sharedEdit.putBoolean("loggedIn", false)
                                sharedEdit.apply()
                            }
                        }
                    }

                    override fun onError(message: String?) {
                        message?.let {
                            progressBar.visibility = View.GONE
                            binding.adminIDInput.isEnabled = true
                            binding.adminPASSInput.isEnabled = true
                            binding.authenticateButton.isEnabled = true
                            MaterialAlertDialogBuilder(this@LoginActivity)
                                .setTitle("Something isn't right!")
                                .setMessage(message)
                                .setNeutralButton("Exit") { dialog, which ->
                                    finish()
                                }
                                .setPositiveButton("Try again") { dialog, which ->
                                    // Respond to positive button press
                                }
                                .show()
                        }
                    }

                })
            }
        }
    }

    private fun loginSuccess(id: String, password: String, token: String) {
        val loginShared = getSharedPreferences("isLogin", MODE_PRIVATE)
        val loginEdit = loginShared.edit()
        loginEdit.putString("id", id)
        loginEdit.putString("pass", password)
        loginEdit.putString("token", token)
        loginEdit.putBoolean("loggedIn", true)

        loginEdit.apply()
        progressBar.visibility = View.GONE
        binding.adminIDInput.isEnabled = true
        binding.adminPASSInput.isEnabled = true
        binding.authenticateButton.isEnabled = true
        startMain()
    }

    private fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}