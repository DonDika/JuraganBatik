package com.dondika.juraganbatik.ui.user.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.databinding.FragmentAccountBinding
import com.dondika.juraganbatik.ui.auth.AuthActivity
import com.dondika.juraganbatik.utility.Utils


class AccountUserFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
        setupListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setupListener() {
        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun setData() {
        binding.apply {
            tvUserName.text = pref.getString(Utils.PREF_NAME).toString()
            tvUserEmail.text = pref.getString(Utils.PREF_EMAIL).toString()
            tvUserNum.text = pref.getString(Utils.PREF_PHONE_NUM).toString()
            tvUserRole.text = pref.getString(Utils.PREF_ROLES).toString()
        }
    }

    private fun logout() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("KELUAR")
            setMessage("Apakah anda yakin ingin keluar?")
            setNegativeButton("Batal"){dialogInterface, _->
                dialogInterface.dismiss()
            }
            setPositiveButton("Keluar"){dialogInterface, _->
                pref.clearUser()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
                requireActivity().finish()
                dialogInterface.dismiss()
            }
            alertDialog.show()
        }
    }

}