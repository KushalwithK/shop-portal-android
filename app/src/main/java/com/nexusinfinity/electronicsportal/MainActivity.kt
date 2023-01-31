package com.nexusinfinity.electronicsportal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.nexusinfinity.electronicsportal.constants.Constant
import com.nexusinfinity.electronicsportal.databinding.ActivityMainBinding
import java.security.Permission
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: NavController
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fragmentView: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC)

        bottomNav = binding.bottomNavigation
        fragmentView = supportFragmentManager.findFragmentById(R.id.navControllerFragment) as NavHostFragment

        controller = fragmentView.navController
        bottomNav.setupWithNavController(controller)

        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.logout -> {
                    logout()
                    return@setOnItemSelectedListener true
                }
                else -> {
                    it.onNavDestinationSelected(controller)
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun logout() {
        val shared = getSharedPreferences("isLogin", MODE_PRIVATE)
        val sharedEdit = shared.edit()
        sharedEdit.putBoolean("loggedIn", false)
        sharedEdit.putString("id", null)
        sharedEdit.putString("pass", null)
        sharedEdit.putString("token", null)

        sharedEdit.apply()

        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}