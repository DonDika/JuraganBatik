package com.dondika.juraganbatik.ui.admin.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dondika.juraganbatik.utility.constant.Firebase.PRODUCTS
import com.dondika.juraganbatik.databinding.FragmentAddDataBinding
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.utility.constant.Firebase.ProductImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.Exception

class AddDataFragment : Fragment() {

    private var _binding: FragmentAddDataBinding? = null
    private val binding get() = _binding!!
    private val productsCollectionRef = Firebase.firestore.collection(PRODUCTS)
    private val productImageRef = Firebase.storage.reference

    private var getUri: Uri? = null
    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                //getFile = file
                binding.imagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
            /*val imageBitmap = it.data?.extras?.get("data") as Bitmap
            binding.imagePreview.setImageBitmap(imageBitmap)*/
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            getUri = selectedImg
            binding.imagePreview.setImageURI(selectedImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION
            )
        }
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

    private fun setListener(){
       binding.apply {
           filledButton.setOnClickListener {
               addData()
           }
           /*cameraButton.setOnClickListener {
               startTakePhoto()
           }*/
           mediaButton.setOnClickListener {
               startGallery()
           }
       }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcherIntentCamera.launch(intent)
    }


    private fun addData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            //val id = get id/username from datastore
            val batikName = binding.batikName.editText?.text.toString()
            val batikPrice = binding.batikPrice.editText?.text.toString()
            val batikAmount = binding.batikAmount.editText?.text.toString()
            val photoRef = productImageRef.child("$ProductImage/$batikName.jpg")
            if (batikName.isEmpty() || batikAmount.isEmpty() || batikPrice.isEmpty() || getUri == null){
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Tolong lengkapi data!", Toast.LENGTH_SHORT).show()
                }
            } else {
                photoRef.putFile(getUri!!)
                    .continueWithTask {
                        photoRef.downloadUrl
                    }.continueWithTask {
                        val batikImg = it.result.toString()
                        val products = Products(
                            batikName, batikPrice, batikAmount, batikImg
                        )
                        productsCollectionRef.add(products)
                    }.await()
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Berhasil upload gambar", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(),e.message, Toast.LENGTH_SHORT).show()
                Log.e("TES Firestore", e.message.toString() )
            }
        }
    }


    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }


}