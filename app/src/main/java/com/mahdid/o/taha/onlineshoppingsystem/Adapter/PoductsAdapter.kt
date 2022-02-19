package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.MainActivity2
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.products
import com.mahdid.o.taha.onlineshoppingsystem.productAdding
import kotlinx.android.synthetic.main.activity_admin_p_br.*
import kotlinx.android.synthetic.main.recycler_item_products.view.*

class PoductsAdapter(val context: Context, val list: ArrayList<products>) :
    RecyclerView.Adapter<PoductsAdapter.viewHolder>() {
    lateinit var db: FirebaseFirestore
    class viewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image = item.image
        var title = item.Title
        var price = item.pprice
        var card = item.product_card
        var rate = item.rate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.recycler_item_products, parent, false)

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
            var name = list[position].name.toString()
            val popupMenu = PopupMenu(context, holder.card)
            popupMenu.menuInflater.inflate(R.menu.product_update_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.Update -> {
                        var name = list[position].name.toString()
                        val intent = Intent(context, productAdding::class.java)
                        intent.putExtra("product_name", name)
                        intent.putExtra("Opp", "update")
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                    R.id.Delete -> {
                        deleteProduct(name)
                        val intent = Intent(context, MainActivity2::class.java)
                        intent.putExtra("added", "done")
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                }
                true
            }
            popupMenu.show()
        }
        holder.rate.rating = list[position].rate
    }

    private fun deleteProduct(name: String) {
        val query =
            db.collection("Products").whereEqualTo("name", name)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    db.collection("Products").document(document.id).delete()
                }
                Toast.makeText(context, "Deleting Product...", Toast.LENGTH_SHORT)
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
}
