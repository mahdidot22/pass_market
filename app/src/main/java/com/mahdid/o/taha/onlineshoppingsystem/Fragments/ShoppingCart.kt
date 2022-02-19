package com.mahdid.o.taha.onlineshoppingsystem.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Adapter.CartAdapter
import com.mahdid.o.taha.onlineshoppingsystem.MainActivity
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.history
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_order_history.view.*
import kotlinx.android.synthetic.main.fragment_shopping_cart.*
import kotlinx.android.synthetic.main.fragment_shopping_cart.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShoppingCart : Fragment() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root = inflater.inflate(R.layout.fragment_shopping_cart, container, false)
        var list = arrayListOf<products>()

        if (haveNetworkConnection()) {
            if (list.size != null) {
                getProducts(root, list)
            } else {
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

    private fun getProducts(roor: View, list: ArrayList<products>) {
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
                            var username = documents.getString("name")
                            Toast.makeText(
                                context,
                                "Checking goes Successfully!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            showDialog()
                            db.collection("Cart")
                                .whereEqualTo(
                                    "email",
                                    e
                                )
                                .get().addOnSuccessListener { qeury ->
                                    if (qeury.documents.isNotEmpty()) {
                                        var toal_price = 0.0
                                        for (document in qeury) {

                                            var p_name = document.getString("name")

                                            var Imge = document.getString("imge")
                                            var price = document.getString("price")
                                            var rate = document.getString("rate")
                                            var id = document.getString("products_id")

                                            Thread {
                                                var listimg =
                                                    Picasso.get().load(Uri.parse(Imge)).get()
                                                Handler(Looper.getMainLooper()).postDelayed(
                                                    {
                                                        list.add(
                                                            products(
                                                                p_name,
                                                                "",
                                                                listimg,
                                                                price!!.toDouble(),
                                                                rate!!.toFloat(),
                                                                null
                                                            )
                                                        )

                                                        val adapter = CartAdapter(context!!, list)
                                                        roor.cart_recycler.layoutManager =
                                                            LinearLayoutManager(context)
                                                        roor.cart_recycler.adapter = adapter

                                                        toal_price += (price.toDouble())
                                                        cart_checkout.text = "Checking out..."
                                                        total.text = "Total : $toal_price USD"

                                                        hideDialog()
                                                        val date_n = SimpleDateFormat(
                                                            "dd MMM, yyyy",
                                                            Locale.getDefault()
                                                        ).format(
                                                            Date()
                                                        )
                                                        val history = hashMapOf(
                                                            "email" to e,
                                                            "total" to price.toString(),
                                                            "id" to id,
                                                            "date" to date_n,
                                                            "username" to username
                                                        )
                                                        db.collection("History").add(history)
                                                            .addOnSuccessListener {
                                                                cart_checkout.text = "Checkout!!"
                                                            }
                                                            .addOnFailureListener { exception ->
                                                                Toast.makeText(
                                                                    context,
                                                                    "Wrong Trying!!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                    },
                                                    3000
                                                )
                                            }.start()
                                        }
                                    } else {
                                        hideDialog()
                                        Toast.makeText(
                                            context,
                                            "Your Cart list is empty!!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    cart_checkout.setOnClickListener {
                                        deleteCart(e)
                                        val intent = Intent(
                                            context,
                                            MainActivity::class.java
                                        )
                                        intent.putExtra("back", "p")
                                        startActivity(intent)
                                        activity!!.finish()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Wrong E-mail!!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                    } else {
                        Toast.makeText(context, "Invalid user!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener {
                    hideDialog()
                    Toast.makeText(context, "Wrong E-mail!!", Toast.LENGTH_SHORT).show()
                }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun deleteCart(e: String) {
        val query =
            db.collection("Cart").whereEqualTo("email", e)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    db.collection("Cart").document(document.id).delete()
                }
                Toast.makeText(context, "Deleting Cart...", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    context,
                    "Unable to deleted this row!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
