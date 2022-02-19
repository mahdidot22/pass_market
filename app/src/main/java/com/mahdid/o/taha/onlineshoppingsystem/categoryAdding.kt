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
import kotlinx.android.synthetic.main.activity_category_adding.*
import kotlinx.android.synthetic.main.activity_category_adding.pname
import java.io.ByteArrayOutputStream

class categoryAdding : AppCompatActivity() {
    lateinit var db: FirebaseFirestore

    private val PICK_IMAGE_REQUEST = 123
    var imageURI: Uri? = null

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_adding)

        db = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

        var update = intent.getStringExtra("update")
        var Category_name_update = intent.getStringExtra("Category_name_update")

        if (update == "done") {
            getCategoryByName(Category_name_update!!)
            id_add_image.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }

            add_to_categories.setOnClickListener {
                if (pname.text.isNotEmpty()) {
                    updateCategory(Category_name_update, pname.text.toString(), imageRef)
                } else {
                    add_to_categories_Err.text = "Fill fields!!"
                }
            }
        } else {
            add_to_categories.setOnClickListener {
                if (pname.text.isNotEmpty()) {
                    addToCategory(imageRef)
                } else {
                    add_to_categories_Err.text = "Fill fields!!"
                }
            }

            id_add_image.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            id_add_image.setImageURI(imageURI)
        }
    }

    private fun addToCategory(imageRef: StorageReference) {
        // Get the data from an ImageView as bytes
        add_to_categories_Err.text = "Uploading image ..."
        val bitmap = (id_add_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val childRef = imageRef.child(
            System.currentTimeMillis().toString() + "_categories_images.png"
        )
        var uploadTask = childRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            add_to_categories_Err.text = "Image Uploaded Failures!!"
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...

            add_to_categories_Err.text = "Image Uploaded Successfully!!"
            childRef.downloadUrl.addOnSuccessListener { uri ->

                addCategory(pname.text.toString(), uri.toString())
                pname.text.clear()
                val intent = Intent(this, MainActivity2::class.java)
                intent.putExtra("added", "CM")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun addCategory(name: String, Image: String) {

        val Category = hashMapOf("name" to name, "image" to Image)

        db.collection("Categories")
            .add(Category)
            .addOnSuccessListener { documentReference ->
                add_to_categories_Err.text = "Category added Successfully!!"
            }
            .addOnFailureListener { exception ->
                add_to_categories_Err.text = "Category added Fields!!"
            }
    }

    private fun updateCategory(
        oldName: String,
        name: String,
        imgeRef: StorageReference
    ) {
        // Get the data from an ImageView as bytes
        add_to_categories_Err.text = "Uploading image ..."
        val bitmap = (id_add_image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val childRef = imgeRef.child(
            System.currentTimeMillis().toString() + "_products_images.png"
        )
        var uploadTask = childRef.putBytes(data)
        uploadTask.addOnFailureListener { exception ->
            add_to_categories_Err.text = "Image Uploaded Failures!!"
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            add_to_categories_Err.text = "Image Uploaded Successfully!!"
            childRef.downloadUrl.addOnSuccessListener { uri ->
                val category = HashMap<String, Any>()
                category["name"] = name

                db.collection("Categories").whereEqualTo("name", oldName).get()
                    .addOnSuccessListener { querySnapshot ->
                        for (documents in querySnapshot) {
                            category["image"] = documents.getString("image").toString()
                            var cn = documents.getString("categoryName")
                            var id = documents.id
                            db.collection("Categories").document(id).update(category)
                            pname.text.clear()
                            val intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("added", "updated")
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener { exception ->
                        add_to_categories_Err.text = "$exception"
                    }
            }
        }
    }

    private fun getCategoryByName(name: String) {
        add_to_categories.text = "Update Category"
        showDialog()
        db.collection("Categories").whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    var image = document.getString("image")
                    var name = document.getString("name")
                    Thread {
                        var listimg = Picasso.get().load(Uri.parse(image)).get()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                id_add_image.setImageBitmap(listimg)
                                pname.setText(name)
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
