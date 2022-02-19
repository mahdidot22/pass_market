package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Product_detiles
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import kotlinx.android.synthetic.main.users_product_item.view.*

class UserCategoryProductAdapter(val context: Context, val list: ArrayList<products>) :
    RecyclerView.Adapter<UserCategoryProductAdapter.viewHolder>() {
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

            val intent = Intent(context, Product_detiles::class.java)
            var name = list[position].name.toString()
            intent.putExtra("back", "ci")
            intent.putExtra("pr_name", name)
            context.startActivity(intent)
            (context as Activity).finish()
        }
        holder.rate.rating = list[position].rate
    }
}
