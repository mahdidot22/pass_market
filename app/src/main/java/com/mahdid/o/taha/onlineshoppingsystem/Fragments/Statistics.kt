package com.mahdid.o.taha.onlineshoppingsystem.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_shopping_cart.*
import kotlinx.android.synthetic.main.fragment_shopping_cart.view.*
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import java.util.*

class Statistics : Fragment() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)

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
        showDialog()
        db.collection("History").orderBy("username")
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

                        val History_Adapter =
                            History_Adapter(context!!, list)
                        root.Statistics_history_list.adapter = History_Adapter

                        hideDialog()
                    }
                } else {
                    hideDialog()
                    Toast.makeText(
                        context,
                        "There is no orders yet!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "there is no orders yet!!", Toast.LENGTH_SHORT).show()
            }
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
