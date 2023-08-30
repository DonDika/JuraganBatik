package com.dondika.juraganbatik.ui.admin.add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.databinding.FragmentUpdateBinding
import com.dondika.juraganbatik.ui.admin.home.HomeAdminFragment
import com.dondika.juraganbatik.utility.Utils
import com.dondika.juraganbatik.utility.constant.Firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private var getUri: Uri? = null

    private lateinit var productData: ProductsResponse

    private val productsCollectionRef = Firebase.firestore.collection(Firestore.PRODUCTS)

    private val pref by lazy { PreferenceManager(requireContext()) }

    private val productImageRef = Firebase.storage.reference



    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            getUri = selectedImg
            binding.imagePreview.setImageURI(selectedImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION){
            if (!allPermissionsGranted()){
                Toast.makeText(requireContext(), "Not getting permission", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all{
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setListener() {
        binding.apply {
            addButton.setOnClickListener {
                updateData()
            }
            mediaButton.setOnClickListener {
                startGallery()
            }
        }
    }

    private fun setData() {
        productData = arguments?.getParcelable(EXTRA_PRODUCT)!!
        binding.apply {
            Glide.with(requireActivity())
                .load(productData.batikImg)
                .into(imagePreview)
            edtInputBatikName.setText(productData.batikName)
            edtInputBatikPrice.setText(productData.batikPrice)
            edtInputBatikAmount.setText(productData.batikAmount)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }


    private fun updateData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val sellerName = pref.getString(Utils.PREF_NAME)!! //"Toko batik solo"//get id/username from datastore
            val sellerEmail = pref.getString(Utils.PREF_EMAIL)!!
            val batikName = binding.edtInputBatikName.text.toString().uppercase()
            val batikPrice = binding.edtInputBatikPrice.text.toString()
            val batikAmount = binding.edtInputBatikAmount.text.toString()
            val batikImg = productData.batikImg
            val photoRef = productImageRef.child("${Firestore.ProductImage}/$batikName.jpg")


            if (batikName.isEmpty() || batikAmount.isEmpty() || batikPrice.isEmpty()){
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Silahkan lengkapi data terlebih dahulu!", Toast.LENGTH_SHORT).show()
                }
            } else if (getUri == null){
                val products = Products(
                    sellerName, sellerEmail, batikName, batikPrice, batikAmount, batikImg
                )
                productsCollectionRef.document(productData.id).set(products)
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Berhasil memperbarui produk", Toast.LENGTH_SHORT).show()
                    closePage()
                }
            }

            else {
                photoRef.putFile(getUri!!)
                    .continueWithTask {
                        photoRef.downloadUrl
                    }.continueWithTask {
                        val uriBatikImg = it.result.toString()
                        val products = Products(
                            sellerName, sellerEmail, batikName, batikPrice, batikAmount, uriBatikImg
                        )
                        productsCollectionRef.document(productData.id).set(products)
                    }.await()
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Berhasil memperbarui produk", Toast.LENGTH_SHORT).show()
                    closePage()
                }
            }

            productsCollectionRef
                .document(productData.id)
                .update("batikName", batikName)

        }catch (e: Exception){

        }
    }



    private fun closePage() {
        val homeFragment = HomeAdminFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.navHostFragment, homeFragment, HomeAdminFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }


}