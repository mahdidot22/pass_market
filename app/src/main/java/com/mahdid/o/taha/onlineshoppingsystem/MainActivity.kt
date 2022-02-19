package com.mahdid.o.taha.onlineshoppingsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahdid.o.taha.onlineshoppingsystem.Fragments.Category
import com.mahdid.o.taha.onlineshoppingsystem.Fragments.Order_history
import com.mahdid.o.taha.onlineshoppingsystem.Fragments.Products
import com.mahdid.o.taha.onlineshoppingsystem.Fragments.ShoppingCart
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nav_view_main.setOnNavigationItemSelectedListener(mainOnNavigationItemSelectedListener)
        val back = intent.getStringExtra("back")
        when (back) {
            "pd" -> {
                ReplaceFragment(Products())
            }
            "c" -> {
                ReplaceFragment(Category())
            }
            "cart" -> {

                ReplaceFragment(ShoppingCart())
            }
        }
    }

    private val mainOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_products -> {
                    ReplaceFragment(Products())

                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_category -> {
                    ReplaceFragment(Category())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_cart -> {
                    ReplaceFragment(ShoppingCart())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_history -> {
                    ReplaceFragment(Order_history())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun ReplaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment).commit()
    }
}
