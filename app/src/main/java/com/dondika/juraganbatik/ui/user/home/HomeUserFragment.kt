package com.dondika.juraganbatik.ui.user.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.databinding.FragmentHomeUserBinding
import com.dondika.juraganbatik.ui.user.detail.DetailCatalogActivity
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
    private val productsCollectionRef = Firebase.firestore.collection(com.dondika.juraganbatik.utility.constant.Firestore.PRODUCTS)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setData()
        setListener()
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
            override fun onItemClicked(products: ProductsResponse) {
                selectedItem(products)
            }
        })
    }

    private fun setListener() {
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchCatalog.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(inputSearch: String): Boolean {
                    clearFocus()
                    searchData(inputSearch)
                    return true
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun setData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val productList: ArrayList<ProductsResponse> = arrayListOf()
            val listOfProducts = productsCollectionRef
                .get()
                .await()
            for (document in listOfProducts.documents){
                val listProducts = ProductsResponse (
                    id = document.reference.id,
                    sellerName =  document.data!!["sellerName"].toString(),
                    sellerEmail =  document.data!!["sellerEmail"].toString(),
                    batikName = document.data!!["batikName"].toString(),
                    batikPrice = document.data!!["batikPrice"].toString(),
                    batikImg =  document.data!!["batikImg"].toString(),
                    batikAmount =  document.data!!["batikAmount"].toString(),
                )
                productList.add(listProducts)
            }
            withContext(Dispatchers.Main) {
                listCatalogUserAdapter.setListCatalog(productList)
            }
        } catch (e: Exception){

        }
    }


    private fun searchData(search: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputSearch = search.uppercase()
            val productList: ArrayList<ProductsResponse> = arrayListOf()
            val listOfProducts = productsCollectionRef
                .whereEqualTo("batikName", inputSearch)
                .get()
                .await()
            for (document in listOfProducts.documents){
                val listProducts = ProductsResponse (
                    id = document.reference.id,
                    sellerName =  document.data!!["sellerName"].toString(),
                    sellerEmail = document.data!!["sellerEmail"].toString(),
                    batikName = document.data!!["batikName"].toString(),
                    batikPrice = document.data!!["batikPrice"].toString(),
                    batikImg =  document.data!!["batikImg"].toString(),
                    batikAmount =  document.data!!["batikAmount"].toString(),
                )
                productList.add(listProducts)
            }
            withContext(Dispatchers.Main){
                listCatalogUserAdapter.setListCatalog(productList)
            }
        } catch (e: Exception){

        }
    }



    private fun selectedItem(products: ProductsResponse){
        val intent = Intent(requireActivity(), DetailCatalogActivity::class.java)
        intent.putExtra(DetailCatalogActivity.EXTRA_PRODUCT, products)
        startActivity(intent)
    }


}
