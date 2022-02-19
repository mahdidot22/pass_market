package com.mahdid.o.taha.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    lateinit var nameHolder: EditText
    lateinit var passwordHolder: EditText
    lateinit var ErrHolder: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = Firebase.firestore

        btn_login.setOnClickListener {
            nameHolder = login_ed_name
            passwordHolder = login_ed_password
            ErrHolder = Login_Err
            if (nameHolder.text.isEmpty() || passwordHolder.text.isEmpty()) {
                ErrHolder.text = "Fill Fields!!"
            } else {
                ErrHolder.text = "Checking..."
                if ((nameHolder.text.toString().compareTo("admin", true) == 0) && (passwordHolder.text.toString().compareTo("admin", true) == 0)
                ) {
                    passwordHolder.text.clear()
                    nameHolder.text.clear()
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    db.collection("marketUsers")
                        .whereEqualTo("name", nameHolder.text.toString())
                        .whereEqualTo("password", passwordHolder.text.toString())
                        .get()
                        .addOnSuccessListener {
                            if (it.size() == 0) {
                                passwordHolder.text.clear()
                                ErrHolder.text = "Invalid username or password!!"
                            } else {
                                val data = Intent("user")
                                data.putExtra("name", nameHolder.text.toString())
                                data.putExtra("password", passwordHolder.text.toString())
                                sendBroadcast(data)

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            passwordHolder.text.clear()
                            ErrHolder.text = "Unexpected Error!!"
                        }
                }
            }
        }

        login_sign_up_page.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }
}
