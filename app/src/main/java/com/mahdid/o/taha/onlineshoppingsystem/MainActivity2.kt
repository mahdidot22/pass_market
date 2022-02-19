package com.mahdid.o.taha.onlineshoppingsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mahdid.o.taha.onlineshoppingsystem.Fragments.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        nav_view_admin.setOnNavigationItemSelectedListener(mainOnNavigationItemSelectedListener)
        when (intent.getStringExtra("added")) {
            "CM" -> {
                ReplaceFragment(CategoryManagement())
            }
            "after_delete" -> {
                ReplaceFragment(Statistics())
            }
            "done" -> {
                ReplaceFragment(CategoryManagement())
            }
            "updated" -> {
                ReplaceFragment(CategoryManagement())
            }
        }
    }

    private val mainOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_category -> {
                    ReplaceFragment(CategoryManagement())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_Statistics -> {
                    ReplaceFragment(Statistics())

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun ReplaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer_admin, fragment).commit()
    }
}
