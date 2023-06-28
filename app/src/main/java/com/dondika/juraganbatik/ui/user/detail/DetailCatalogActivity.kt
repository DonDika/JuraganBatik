package com.dondika.juraganbatik.ui.user.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.ActivityDetailCatalogBinding

class DetailCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCatalogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setData()
    }


    private fun setData() {
        val productData = intent.getParcelableExtra<Products>(EXTRA_PRODUCT)
        if (productData!= null){
            binding.apply {
                tvBatikName.text = productData.batikName
                tvBatikPrice.text = "Rp ${productData.batikPrice}"
                tvBatikSeller.text = productData.username
                Glide.with(this@DetailCatalogActivity)
                    .load(productData.batikImg)
                    .into(imgBatik)
            }
        }
    }


    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}