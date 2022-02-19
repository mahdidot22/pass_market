package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Category_items
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.category
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.recycler_item_category.view.*
import kotlinx.android.synthetic.main.users_category_item.view.*
import kotlinx.android.synthetic.main.users_category_item.view.img
import kotlinx.android.synthetic.main.users_category_item.view.pname

class UsersCategoryAdapter(val context: Context, val list: ArrayList<category>) :
    RecyclerView.Adapter<UsersCategoryAdapter.viewHolder>() {
    lateinit var db: FirebaseFirestore

    class viewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var img = item.img
        var name = item.pname
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.users_category_item, parent, false)

        return viewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        db = Firebase.firestore
        holder.img.setImageBitmap(list[position].image)
        holder.name.text = list[position].name
        holder.img.setOnClickListener {
            val intent = Intent(context, Category_items::class.java)
            intent.putExtra(
                "categoryName_browser",
                list[position].name.toString()
            )
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }
}
