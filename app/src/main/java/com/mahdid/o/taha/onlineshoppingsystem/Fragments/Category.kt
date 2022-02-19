package com.mahdid.o.taha.onlineshoppingsystem.Fragments

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mahdid.o.taha.onlineshoppingsystem.Adapter.UsersCategoryAdapter
import com.mahdid.o.taha.onlineshoppingsystem.R
import com.mahdid.o.taha.onlineshoppingsystem.model.category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_category.view.recyclerview_category
import kotlinx.android.synthetic.main.recycler_item_category.view.*

class Category : Fragment() {
    private var progressDialog: ProgressDialog? = null
    var list = arrayListOf<category>()
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = Firebase.firestore
        val root = inflater.inflate(R.layout.fragment_category, container, false)
        if (haveNetworkConnection()) {
            showDialog()
            getCategoryList(root)
        } else {
            Toast.makeText(context, "Fix your internet Connection!!", Toast.LENGTH_SHORT)
                .show()
        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        val inflater = inflater
        inflater.inflate(R.menu.search, menu)
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.option_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchView.clearFocus()
                    searchView.setQuery("", false)
                    searchMenuItem.collapseActionView()
                    if (haveNetworkConnection()) {
                        showDialog()
                    } else {
                        Toast.makeText(
                            context,
                            "Fix your internet Connection!!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    return false
                }
            }
        )
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(context)
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
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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

    private fun getCategoryList(root: View) {
        db.collection("Categories")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    for (document in querySnapshot) {
                        var image = document.getString("image")
                        var name = document.getString("name")
                        Thread {
                            var listimg = Picasso.get().load(Uri.parse(image)).get()
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    list.add(
                                        category(
                                            listimg,
                                            name
                                        )
                                    )
                                    var adapter = UsersCategoryAdapter(context!!, list)
                                    root.recyclerview_category.layoutManager =
                                        LinearLayoutManager(context)
                                    root.recyclerview_category.adapter = adapter
                                    hideDialog()
                                },
                                3000
                            )
                        }.start()
                    }
                } else {
                    hideDialog()
                    Toast.makeText(context, "Collection is empty!!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { exception ->
                hideDialog()
                Toast.makeText(
                    context,
                    "Fix your internet Connection!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
    }
}
