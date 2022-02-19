package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.AdminPBr
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.categoryAdding
import com.mahdid.o.taha.onlineshoppingsystem.model.category
import com.mahdid.o.taha.onlineshoppingsystem.productAdding
import kotlinx.android.synthetic.main.recycler_item_category.view.*

class CategoryAdapter(val context: Context, val list: ArrayList<category>) :
    RecyclerView.Adapter<CategoryAdapter.viewHolder>() {
    lateinit var db: FirebaseFirestore

    class viewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var img = item.img
        var name = item.pname
        var buttonViewOption = item.textViewOptions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater =
            LayoutInflater.from(context).inflate(R.layout.recycler_item_category, parent, false)

        return viewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        db = Firebase.firestore
        holder.img.setImageBitmap(list[position].image)
        holder.name.text = list[position].name
        holder.buttonViewOption.setOnClickListener {
            val popup = PopupMenu(context, holder.buttonViewOption)
            popup.inflate(R.menu.update_delete)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.Browser -> {
                        val intent = Intent(context, AdminPBr::class.java)
                        intent.putExtra(
                            "categoryName_browser",
                            list[position].name.toString()
                        )
                        context.startActivity(intent)
                    }
                    R.id.Add -> {
                        var name = list[position].name.toString()
                        val intent = Intent(context, productAdding::class.java)
                        intent.putExtra("Category_name_add", name)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                    R.id.Update -> {
                        var name = list[position].name.toString()
                        val intent = Intent(context, categoryAdding::class.java)
                        intent.putExtra("Category_name_update", name)
                        intent.putExtra("update", "done")
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
                    R.id.Delete -> {
                        var name = list[position].name.toString()
                        var intent = Intent(context, AdminPBr::class.java)
                        intent.putExtra("Category_name_delete", name)
                        intent.putExtra("Opp", "delete")
                        context.startActivity(intent)
                        deleteCategory(name)
                        (context as Activity).finish()
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun deleteCategory(name: String) {
        val query =
            db.collection("Categories").whereEqualTo("name", name)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    db.collection("Categories").document(document.id).delete()
                }
                Toast.makeText(context, "Deleting Category...", Toast.LENGTH_SHORT)
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
