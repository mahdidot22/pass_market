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
import com.mahdid.o.taha.onlineshoppingsystem.Adapter.PoductsAdapter
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_p_br.*
import kotlinx.android.synthetic.main.fragment_category_management.*
import kotlinx.android.synthetic.main.recycler_item_products.view.*

class AdminPBr : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_p_br)

        db = Firebase.firestore

        if (haveNetworkConnection()) {
            var list = arrayListOf<products>()
            val categoryName_browser = intent.getStringExtra("categoryName_browser")
            val Category_name_delete = intent.getStringExtra("Category_name_delete")
            val Category_name_product_update = intent.getStringExtra("Category_name_product_update")
            val opp = intent.getStringExtra("Opp")
            val update = intent.getStringExtra("update")
            showDialog()

            if (opp == "delete") {
                deleteProductsDependOnCategory(Category_name_delete)
            } else {
                if (update == "done") {
                    getProduct(Category_name_product_update, list)
                } else {
                    getProduct(categoryName_browser, list)
                }
            }
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

                                    val adapter = PoductsAdapter(this, list)
                                    recyclerview_pManagements.layoutManager =
                                        GridLayoutManager(this, 2)
                                    recyclerview_pManagements.adapter = adapter

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

    private fun deleteProductsDependOnCategory(Category_name: String) {
        val query =
            db.collection("Products").whereEqualTo("categoryName", Category_name)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    db.collection("Products").document(document.id).delete()
                }
                Toast.makeText(this, "Deleting items...", Toast.LENGTH_SHORT)
                    .show()

                var intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("added", "after_delete")
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Unable to deleted these category products!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Unable to deleted these category products!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
