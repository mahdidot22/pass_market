package com.mahdid.o.taha.onlineshoppingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    lateinit var ErrHolder: Button
    lateinit var nameHolder: EditText
    lateinit var emailHolder: EditText
    lateinit var passwordHolder: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        db = Firebase.firestore
        btn_sign_up.setOnClickListener {
            nameHolder = sign_up_ed_name
            emailHolder = sign_up_ed_email
            passwordHolder = sign_up_ed_password
            ErrHolder = Sign_up_Err

            if (nameHolder.text.isEmpty() || emailHolder.text.isEmpty() || passwordHolder.text.isEmpty()) {
                ErrHolder.text = "Fill Fields!!"
            } else {
                ErrHolder.text = "Checking..."
                db.collection("marketUsers")
                    .whereEqualTo("email", emailHolder.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it.size() == 0) {
                            addUser(
                                nameHolder.text.toString(),
                                emailHolder.text.toString(),
                                passwordHolder.text.toString()
                            )
                        } else {
                            passwordHolder.text.clear()
                            ErrHolder.text = "User already exists!!"
                        }
                    }
                    .addOnFailureListener { exception ->
                        passwordHolder.text.clear()
                        ErrHolder.text = "Unexpected Error!!"
                    }
            }
        }

        sign_up_login_page.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun addUser(name: String, email: String, password: String) {

        val user = hashMapOf("name" to name, "email" to email, "password" to password)

        db.collection("marketUsers")
            .add(user)
            .addOnSuccessListener { documentReference ->
                nameHolder.text.clear()
                passwordHolder.text.clear()
                emailHolder.text.clear()
                ErrHolder.text = "User added Successfully!!"
            }
            .addOnFailureListener { exception ->
                passwordHolder.text.clear()
                ErrHolder.text = "User adding failure!!"
            }
    }
}
