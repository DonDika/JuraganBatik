package com.dondika.juraganbatik.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.databinding.ActivityMainUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivityUser : AppCompatActivity() {

    private lateinit var binding: ActivityMainUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
    }

    private fun setupBottomNav() {
        val bottomNavView: BottomNavigationView = binding.bottomNavUser
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostUserFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.setupWithNavController(navController)
        //bottomNavView.visibility = View.VISIBLE
    }


}