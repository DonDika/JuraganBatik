package com.dondika.juraganbatik.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dondika.juraganbatik.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setupFragment()
    }

    private fun setupFragment() {
        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if(fragment !is LoginFragment){
            fragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, loginFragment, LoginFragment::class.java.simpleName)
                .commit()
        }
    }






}