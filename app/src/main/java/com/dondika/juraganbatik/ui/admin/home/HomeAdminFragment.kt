package com.dondika.juraganbatik.ui.admin.home

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.databinding.FragmentHomeAdminBinding
import com.dondika.juraganbatik.ui.admin.add.AddDataFragment
import com.dondika.juraganbatik.ui.admin.add.UpdateFragment
import com.dondika.juraganbatik.utility.Utils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.dondika.juraganbatik.utility.constant.Firestore.PRODUCTS
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
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
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
        listCatalogAdapter = ListCatalogAdapter()
        binding.rvCatalog.apply {
            adapter = listCatalogAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
        listCatalogAdapter.setOnItemClickCallback(object: ListCatalogAdapter.OnItemClickCallback{
            override fun onItemClicked(products: ProductsResponse) {
                selectedItem(products)
            }

            override fun onItemDeleted(products: ProductsResponse) {
                deleteItem(products)
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

        binding.fabAdd.setOnClickListener {
            val fragmentAdd = AddDataFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.navHostFragment, fragmentAdd, AddDataFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }


    private fun setData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val productList: ArrayList<ProductsResponse> = arrayListOf()
            val listOfProducts = productsCollectionRef
                .whereEqualTo("sellerEmail", pref.getString(Utils.PREF_EMAIL))
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
                /*if (listProducts.sellerEmail == pref.getString(Utils.PREF_EMAIL)){

                }*/
            }
            withContext(Dispatchers.Main){
                listCatalogAdapter.setListCatalog(productList)
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
                .whereEqualTo("sellerEmail", pref.getString(Utils.PREF_EMAIL))
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
                /*if (listProducts.batikName == inputSearch &&
                    listProducts.sellerEmail == pref.getString(Utils.PREF_EMAIL)){

                } else {
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "Produk Tidak Ada", Toast.LENGTH_SHORT).show()
                    }
                }*/
            }
            withContext(Dispatchers.Main){
                listCatalogAdapter.setListCatalog(productList)
            }
        } catch (e: Exception){
        }
    }


    private fun selectedItem(products: ProductsResponse) {
        val updateDataFragment = UpdateFragment()
        val bundle = Bundle()
        bundle.putParcelable(UpdateFragment.EXTRA_PRODUCT, products)
        updateDataFragment.arguments = bundle

        val fragmentManager = parentFragmentManager
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.navHostFragment, updateDataFragment, UpdateFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    private fun deleteItem(products: ProductsResponse) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("Hapus Catalog")
            setMessage("Hapus ${products.batikName} dari catalog?")
            setNegativeButton("Batal"){dialogInterface, _->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus"){dialogInterface, _->
                deleteCatalog(products.id)
                dialogInterface.dismiss()
            }
            alertDialog.show()
        }
    }

    private fun deleteCatalog(productId: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            productsCollectionRef.document(productId).delete()
            setData()
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(), "Produk katalog berhasil dihapus!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception){

        }
    }



}