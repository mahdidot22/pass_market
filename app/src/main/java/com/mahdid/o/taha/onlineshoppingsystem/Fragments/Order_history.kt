package com.mahdid.o.taha.onlineshoppingsystem.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Adapter.History_Adapter
import com.mahdid.o.taha.onlineshoppingsystem.MainActivity
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.history
import kotlinx.android.synthetic.main.activity_order_history.view.*
import kotlinx.android.synthetic.main.fragment_category.view.*
import kotlinx.android.synthetic.main.fragment_shopping_cart.*
import kotlinx.android.synthetic.main.fragment_shopping_cart.view.*
import java.util.*

class Order_history : Fragment() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root = inflater.inflate(R.layout.activity_order_history, container, false)
        val data = mutableListOf<history>()

        if (haveNetworkConnection()) {

            if (data.size != null) {
                getHistory(root, data as ArrayList<history>)
            } else {
                Toast.makeText(context, "There is no orders yet!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
        } else {
            Toast.makeText(context, "Fix your internet Connection!!", Toast.LENGTH_SHORT)
                .show()
        }

        return root
    }

    private fun getHistory(root: View, list: ArrayList<history>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Add your email: ")

        val email = EditText(context)

        email.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        builder.setView(email)

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->

            var e = email.text.toString()
            db.collection("marketUsers")
                .whereEqualTo("email", e)
                .get().addOnSuccessListener {
                    if (it.documents.isNotEmpty()) {
                        for (documents in it) {
                            Toast.makeText(
                                context,
                                "Checking goes Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            showDialog()
                            db.collection("History")
                                .whereEqualTo(
                                    "email",
                                    e
                                )
                                .get().addOnSuccessListener { qeury ->
                                    if (qeury.documents.isNotEmpty()) {
                                        for (document in qeury) {
                                            var id = document.getString("id")
                                            var price = document.getString("total")
                                            var date = document.getString("date")
                                            var username = document.getString("username")
                                            list.add(
                                                history(
                                                    username.toString(),
                                                    id.toString(),
                                                    price.toString(),
                                                    date.toString()
                                                )
                                            )

                                            val History_Adapter = History_Adapter(context!!, list)
                                            root.history_list.adapter = History_Adapter

                                            hideDialog()
                                        }
                                    } else {
                                        hideDialog()
                                        Toast.makeText(
                                            context,
                                            "Your order list is empty!!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }.addOnFailureListener {
                                    hideDialog()
                                    Toast.makeText(context, "Wrong E-mail!!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Invalid user!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Wrong E-mail!!", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val netInfo = cm!!.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName
                .equals("WIFI", ignoreCase = true)
            ) if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName
                .equals("MOBILE", ignoreCase = true)
            ) if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage("Fetching data ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}
