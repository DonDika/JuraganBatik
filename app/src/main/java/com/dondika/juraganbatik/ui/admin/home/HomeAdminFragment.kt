package com.dondika.juraganbatik.ui.admin.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.FragmentHomeAdminBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.dondika.juraganbatik.utility.constant.Firebase.PRODUCTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeAdminFragment : Fragment() {

    private var _binding: FragmentHomeAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var listCatalogAdapter: ListCatalogAdapter
    private val productsCollectionRef = Firebase.firestore.collection(PRODUCTS)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setData()
        binding.buyButton.setOnClickListener {

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setAdapter() {
        listCatalogAdapter = ListCatalogAdapter()
        binding.rvCatalog.apply {
            adapter = listCatalogAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun setData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = productsCollectionRef.get().await().toObjects(Products::class.java)
            Log.e( "retrieveDataIO: ", Thread.currentThread().name.toString() )
            withContext(Dispatchers.Main){
                listCatalogAdapter.setListCatalog(querySnapshot)
                Log.e( "retrieveData: ", querySnapshot.toString() )
                Log.e( "retrieveDataMain: ", Thread.currentThread().name.toString() )
            }
            /*
            productsCollectionRef.get().addOnSuccessListener { result ->
                val objectsData = result.toObjects(Products::class.java)
                Log.e( "retrieveData: ", objectsData.toString() )
                listCatalogAdapter.setListCatalog(objectsData)
                Log.e( "retrieveData: ", Thread.currentThread().name.toString() )
             }
            */
        } catch (e: Exception){
            Log.e("Retrieve", e.message.toString() )
        }
    }



}