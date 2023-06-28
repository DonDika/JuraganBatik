package com.dondika.juraganbatik.ui.user.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.FragmentHomeUserBinding
import com.dondika.juraganbatik.ui.user.detail.DetailCatalogActivity
import com.dondika.juraganbatik.ui.user.detail.DetailUserFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var listCatalogUserAdapter: ListCatalogUserAdapter
    private lateinit var parcelableState: Products
    private val productsCollectionRef = Firebase.firestore.collection(com.dondika.juraganbatik.utility.constant.Firebase.PRODUCTS)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setData()
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setAdapter() {
        listCatalogUserAdapter = ListCatalogUserAdapter()
        binding.rvCatalog.apply {
            adapter = listCatalogUserAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
        listCatalogUserAdapter.setOnItemClickCallback(object : ListCatalogUserAdapter.OnItemClickCallback{
            override fun onItemClicked(products: Products) {
                selectedItem(products)
            }
        })
    }

    private fun setData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val productList: ArrayList<Products> = arrayListOf()
            val listOfProducts = productsCollectionRef.get().await() //.toObjects(Products::class.java)
            //Log.e( "retrieveDataIO: ", Thread.currentThread().name.toString() )
            for (document in listOfProducts.documents){
                //val id =
                //document.toObject(Products::class.java)
                val listProducts = Products (
                    id = document.reference.id,
                    batikName = document.data!!["batikName"].toString(),
                    batikPrice = document.data!!["batikPrice"].toString(),
                    batikImg =  document.data!!["batikImg"].toString(),
                    batikAmount =  document.data!!["batikAmount"].toString(),
                    username =  document.data!!["username"].toString()
                )
                //Log.e("TAGid", id )
                Log.e("TAGid", listProducts.toString() )
                //Log.e( "TAGid: ", Thread.currentThread().name.toString() )*/
                productList.add(listProducts)
            }
            withContext(Dispatchers.Main) {
                listCatalogUserAdapter.setListCatalog(productList)
            }

            /*
            productsCollectionRef.get().addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { queryDocumentSnapshot ->
                    val products = Products(
                        id = queryDocumentSnapshot.reference.id,
                        batikAmount = queryDocumentSnapshot.data["batikName"].toString()
                    )
                    Log.e("TAG", products.id )
                }
            }*/


            withContext(Dispatchers.Main){
                //listCatalogUserAdapter.setListCatalog(listOfProducts)


                //Log.e( "retrieveData: ", listOfProducts.toString() )
                Log.e( "retrieveDataMain: ", Thread.currentThread().name.toString() )
            }
        } catch (e: Exception){
            Log.e("Retrieve", e.message.toString() )
        }
    }


    private fun selectedItem(products: Products){
        /*
        val detailUserFragment = DetailUserFragment()
        val bundle = Bundle()

        bundle.putParcelable(DetailUserFragment.EXTRA_PRODUCT, products)
        detailUserFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.navHostUserFragment, detailUserFragment)
            .addToBackStack(null)
            .commit()
        */
        val intent = Intent(requireActivity(), DetailCatalogActivity::class.java)
        intent.putExtra(DetailCatalogActivity.EXTRA_PRODUCT, products)
        startActivity(intent)
    }


}
