package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import kotlinx.android.synthetic.main.recycler_cart_item.view.*

class CartAdapter(val context: Context, val list: ArrayList<products>) :
    RecyclerView.Adapter<CartAdapter.viewHolder>() {

    class viewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var img = item.image
        var title = item.Title
        var price = item.pprice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.recycler_cart_item, parent, false)

        return viewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.title.text = list[position].name
        holder.price.text = "${list[position].price} USD"
        holder.img.setImageBitmap(list[position].image!!)
    }
}
