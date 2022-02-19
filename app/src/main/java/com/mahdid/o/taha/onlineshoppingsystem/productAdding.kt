package com.mahdid.o.taha.onlineshoppingsystem

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_adding.*
import kotlinx.android.synthetic.main.activity_product_adding.pprice
import java.io.ByteArrayOutputStream

class productAdding : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog? = null

    private val PICK_IMAGE_REQUEST = 123
    var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_adding)
        var categoryName = intent.getStringExtra("Category_name_add")
        var product_name = intent.getStringExtra("product_name")
        var Opp = intent.getStringExtra("Opp")
        db = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

        if (Opp == "update") {
            getUsersByName(product_name!!)
            id_add_image_product.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            add_to_products.setOnClickListener {
                if (pname.text.isNotEmpty() && pdescription.text.isNotEmpty() && pprice.text.isNotEmpty()) {
                    updateProduct(
                        product_name,
                        pname.text.toString(),
                        pprice.text.toString(),
                        pdescription.text.toString(),
                        imageRef
                    )
                } else {
                    add_to_products_Err.text = "Fill fields!!"
                }
            }
        } else {
            id_add_image_product.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
            add_to_products.setOnClickListener {
                if (pname.text.isNotEmpty() && pdescription.text.isNotEmpty() && pprice.text.isNotEmpty()) {
                    addToProduct(imageRef, categoryName)
                } else {
                    add_to_products_Err.text = "Fill fields!!"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            id_add_image_product.setImageURI(imageURI)
        }
    }

    private fun addProduct(
        name: String,
        description: String,
        Imge: String,
        price: String,
        rate: String,
        location: String,
        categoryName: String
    ) {

        val Product = hashMapOf(
            "name" to name,
            "description" to description,
            "Imge" to Imge,
            "price" to price,
            "rate" to rate,
            "location" to location,
            "categoryName" to categoryName
        )

        db.collection("Products")
            .add(Product)
            .addOnSuccessListener { documentReference ->
                add_to_products_Err.text = "Product added Successfully!!"
            }
            .addOnFailureListener { exception ->
                add_to_products_Err.text = "Product added Fields!!"
            }
    }

    private fun addToProduct(imageRef: StorageReference, categoryName: String) {
        // Get the data from an ImageView as bytes
        add_to_products_Err.text = "Uploading image ..."
        val bitmap = (id_add_image_product.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val childRef = imageRef.child(
            System.currentTimeMillis().toString() + "_products_images.png"
        )
        var uploadTask = childRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            add_to_products_Err.text = "Image Uploaded Failures!!"
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            add_to_products_Err.text = "Image Uploaded Successfully!!"
            childRef.downloadUrl.addOnSuccessListener { uri ->

                addProduct(
                    pname.text.toString(),
                    pdescription.text.toString(),
                    uri.toString(),
                    pprice.text.toString(),
                    0f.toString(),
                    location.toString(),
                    categoryName
                )
                pname.text.clear()
                pdescription.text.clear()
                pprice.text.clear()
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("added", "Pcm")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateProduct(
        oldName: String,
        name: String,
        price: String,
        description: String,
        imgeRef: StorageReference
    ) {
        // Get the data from an ImageView as bytes
        add_to_products_Err.text = "Uploading image ..."
        val bitmap = (id_add_image_product.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val childRef = imgeRef.child(
            System.currentTimeMillis().toString() + "_products_images.png"
        )
        var uploadTask = childRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            add_to_products_Err.text = "Image Uploaded Failures!!"
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            add_to_products_Err.text = "Image Uploaded Successfully!!"
            childRef.downloadUrl.addOnSuccessListener { uri ->
                val product = HashMap<String, Any>()
                product["name"] = name
                product["price"] = price
                product["description"] = description
                db.collection("Products").whereEqualTo("name", oldName).get()
                    .addOnSuccessListener { querySnapshot ->
                        for (documents in querySnapshot) {
                            product["Imge"] = documents.getString("Imge").toString()
                            var cn = documents.getString("categoryName")
                            var id = documents.id
                            db.collection("Products").document(id).update(product)
                            pname.text.clear()
                            pdescription.text.clear()
                            pprice.text.clear()
                            val intent = Intent(this, AdminPBr::class.java)
                            intent.putExtra("Category_name_product_update", cn)
                            intent.putExtra("update", "done")
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { exception ->
                        add_to_products_Err.text = "$exception"
                    }
            }
        }
    }

    private fun getUsersByName(name: String) {
        add_to_products.text = "Update Products"
        showDialog()
        db.collection("Products").whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    var image = document.getString("Imge")
                    var name = document.getString("name")
                    var price = document.getString("price")
                    var description = document.getString("description")
                    Thread {
                        var listimg = Picasso.get().load(Uri.parse(image)).get()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                id_add_image_product.setImageBitmap(listimg)
                                pname.setText(name)
                                pprice.setText(price)
                                pdescription.setText(description)
                                hideDialog()
                            },
                            3000
                        )
                    }.start()
                }
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
}
