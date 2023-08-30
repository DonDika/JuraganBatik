package com.dondika.juraganbatik.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.databinding.ActivitySplashBinding
import com.dondika.juraganbatik.ui.admin.MainActivityAdmin
import com.dondika.juraganbatik.ui.auth.AuthActivity
import com.dondika.juraganbatik.ui.user.MainActivityUser
import com.dondika.juraganbatik.utility.Utils

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val pref by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.ivLogo.setBackgroundColor(resources.getColor(android.R.color.transparent))
        validateUser()
    }

    private fun validateUser() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (pref.getInt(Utils.PREF_IS_LOGIN) == 1 && pref.getString(Utils.PREF_ROLES) == Utils.PENJUAL){
                startActivity(Intent(this, MainActivityAdmin::class.java))
                finish()
            } else if (pref.getInt(Utils.PREF_IS_LOGIN) == 1 && pref.getString(Utils.PREF_ROLES) == Utils.PEMBELI){
                startActivity(Intent(this, MainActivityUser::class.java))
                finish()
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        },2000)
    }


}