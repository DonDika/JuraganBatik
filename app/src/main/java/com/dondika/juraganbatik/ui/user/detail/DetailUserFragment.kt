package com.dondika.juraganbatik.ui.user.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.FragmentDetailUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class DetailUserFragment : Fragment() {

    private var _binding: FragmentDetailUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setBottomNav()
    }

    private fun setBottomNav() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavUser)
        bottomNavigationView.visibility = View.GONE
    }

    private fun setData() {
        val bundle = arguments
        if (bundle != null){
            val myParcelableObject = bundle.getParcelable<Products>(EXTRA_PRODUCT)
            binding.textDetail.text = myParcelableObject?.id.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }


}