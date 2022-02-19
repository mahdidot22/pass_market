package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Product_detiles
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import kotlinx.android.synthetic.main.activity_order_history.view.*
import kotlinx.android.synthetic.main.users_product_item.view.*

class UsersProductAdapter(val context: Context, val list: ArrayList<products>) :
    RecyclerView.Adapter<UsersProductAdapter.viewHolder>() {
    lateinit var db: FirebaseFirestore

    class viewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image = item.image
        var title = item.Title
        var price = item.pprice
        var card = item.product_card
        var rate = item.rate_user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.users_product_item, parent, false)

        return viewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        db = Firebase.firestore
        holder.image.setImageBitmap(list[position].image!!)
        holder.title.text = list[position].name
        holder.price.text = "${list[position].price} USD"
        holder.card.setOnClickListener {
            val rating: Float = list[position].rate
            db.collection("Products")
                .whereEqualTo("name", list[position].name.toString())
                .get().addOnSuccessListener { qeury ->
                    for (document in qeury) {
                        db.collection("Products").document(document.id)
                            .update("rate", rating.toString())
                    }
                }

            val popupMenu = PopupMenu(context, holder.card)
            popupMenu.menuInflater.inflate(R.menu.info_add_product, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.P_INFO -> {
                        var name = list[position].name.toString()
                        val intent = Intent(context, Product_detiles::class.java)
                        intent.putExtra("pr_name", name)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                    R.id.Add_to_cart -> {

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
                                        Toast.makeText(
                                            context,
                                            "Checking goes Successfully!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        db.collection("Products")
                                            .whereEqualTo(
                                                "name",
                                                list[position].name.toString()
                                            )
                                            .get().addOnSuccessListener { qeury ->
                                                for (document in qeury) {
                                                    var p_name = document.getString("name")
                                                    var Imge = document.getString("Imge")
                                                    var price = document.getString("price")
                                                    var rate = document.getString("rate")

                                                    var products_id = document.id
                                                    val cart = hashMapOf(
                                                        "products_id" to products_id,
                                                        "email" to e,
                                                        "name" to p_name,
                                                        "imge" to Imge,
                                                        "price" to price,
                                                        "rate" to
                                                            rate
                                                    )

                                                    db.collection("Cart")
                                                        .add(cart)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Product added Successfully!!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                        .addOnFailureListener { exception ->
                                                            Toast.makeText(
                                                                context,
                                                                "Product added Fields!!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Invalid user!!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Wrong E-mail!!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                        builder.setNegativeButton(
                            "Cancel"
                        ) { dialog, _ -> dialog.cancel() }
                        builder.show()
                    }
                }
                true
            }
            popupMenu.show()
        }
        holder.rate.rating = list[position].rate
    }
}
