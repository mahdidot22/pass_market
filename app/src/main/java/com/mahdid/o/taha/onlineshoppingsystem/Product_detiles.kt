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
import androidx.core.graphics.get
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_p_br.*
import kotlinx.android.synthetic.main.activity_product_detiles.*
import kotlinx.android.synthetic.main.products_contents.*

class Product_detiles : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detiles)

        db = Firebase.firestore
        bar_product_d.setNavigationOnClickListener {
            var back = intent.getStringExtra("back")
            if (back == "ci") {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("back", "c")
                startActivity(intent)
                finish()
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("back", "pd")
            startActivity(intent)
            finish()
        }

        var pr_name = intent.getStringExtra("pr_name")

        if (haveNetworkConnection()) {
            showDialog()
            getProduct(pr_name)
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

    private fun getProduct(pr_name: String) {
        db.collection("Products")
            .whereEqualTo("name", pr_name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    for (document in querySnapshot) {
                        var image = document.getString("Imge")
                        var name = document.getString("name")
                        var price = document.getString("price")
                        var des = document.getString("description")
                        Thread {
                            var listimg = Picasso.get().load(Uri.parse(image)).get()
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    im.setImageBitmap(listimg)
                                    cost.text = "$price USD"
                                    description.text = des
                                    text.text = name

                                    hideDialog()
                                },
                                3000
                            )
                        }.start()
                    }
                } else {
                    hideDialog()
                    Toast.makeText(this, "Product is not exist!!", Toast.LENGTH_SHORT)
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
