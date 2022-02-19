package com.mahdid.o.taha.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
