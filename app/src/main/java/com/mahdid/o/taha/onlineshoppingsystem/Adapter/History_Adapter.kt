package com.mahdid.o.taha.onlineshoppingsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.history
import kotlinx.android.synthetic.main.list_order_history_item.view.*

class History_Adapter(var context: Context, var list: ArrayList<history>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var root = convertView
        if (root == null)
            root =
                LayoutInflater.from(context).inflate(R.layout.list_order_history_item, null, false)
        root!!.order_id.text = list[position].id
        root!!.order_coast.text = "${list[position].price} USD"
        root!!.order_date.text = "${list[position].date}"
        root.order_owner.text = list[position].name

        return root
    }

    override fun getItem(position: Int): Any {
        return list[position].price.toInt()
    }

    override fun getItemId(position: Int): Long {
        return list[position].price.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}
