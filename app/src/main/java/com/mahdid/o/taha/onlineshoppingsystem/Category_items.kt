package com.mahdid.o.taha.onlineshoppingsystem

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Adapter.UserCategoryProductAdapter
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_category_items.*
import kotlinx.android.synthetic.main.users_product_item.view.*

class Category_items : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_items)
        db = Firebase.firestore
        bar_Category_items.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("back", "c")
            startActivity(intent)
            finish()
        }

        if (haveNetworkConnection()) {
            var list = arrayListOf<products>()
            val categoryName_browser = intent.getStringExtra("categoryName_browser")
            showDialog()
            getProduct(categoryName_browser, list)
        } else {
            Toast.makeText(this, "Fix your internet Connection!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Fetching data ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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

    private fun getProduct(Categoryname: String, list: ArrayList<products>) {
        db.collection("Products")
            .whereEqualTo("categoryName", Categoryname)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    for (document in querySnapshot) {
                        var image = document.getString("Imge")
                        var name = document.getString("name")
                        var price = document.getString("price")
                        var rate = document.getString("rate")
                        Thread {
                            var listimg = Picasso.get().load(Uri.parse(image)).get()
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    list.add(
                                        products(
                                            name,
                                            "",
                                            listimg,
                                            price!!.toDouble(),
                                            rate!!.toFloat(),
                                            null
                                        )
                                    )

                                    val adapter = UserCategoryProductAdapter(this, list)
                                    category_items.layoutManager =
                                        GridLayoutManager(this, 2)
                                    category_items.adapter = adapter
                                    hideDialog()
                                },
                                3000
                            )
                        }.start()
                    }
                } else {
                    hideDialog()
                    Toast.makeText(this, "Category is empty!!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                hideDialog()
                Toast.makeText(
                    this,
                    "Fix your internet Connection!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }
}
