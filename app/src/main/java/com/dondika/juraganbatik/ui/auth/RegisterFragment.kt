package com.dondika.juraganbatik.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.model.UserData
import com.dondika.juraganbatik.databinding.FragmentRegisterBinding
import com.dondika.juraganbatik.utility.constant.Firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val usersCollectionRef = Firebase.firestore.collection(Firestore.USERS)

    private lateinit var selectedRole: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRoles()
        setupListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setupRoles(){
        val roles = resources.getStringArray(R.array.roles)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_role, roles)
        val selectItem = binding.edtSelectRole
        selectItem.apply {
            setAdapter(arrayAdapter)
            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                selectedRole = parent.getItemAtPosition(position) as String
            }
        }
    }


    private fun setupListener() {
        binding.registerButton.setOnClickListener {
            if (isRequired()){
                checkEmail()
            } else {
                Toast.makeText(requireContext(), "Isi data dengan lengkap", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkEmail() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val checkEmail = usersCollectionRef
                .whereEqualTo(EMAIL, binding.edtInputEmail.text.toString())
                .get()
                .await()
            if (checkEmail.isEmpty){
                addUserData()
            } else {
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"email sudah digunakan", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception){

        }
    }


    private fun addUserData() = CoroutineScope(Dispatchers.IO).launch {
        val name = binding.edtInputName.text.toString()
        val email = binding.edtInputEmail.text.toString()
        val password = binding.edtInputPassword.text.toString()
        val phoneNumber = binding.edtInputPhoneNum.text.toString()
        val roles = selectedRole

        try {
            val registerData = UserData(name, email, password, phoneNumber, roles)
            usersCollectionRef.add(registerData).await()
            withContext(Dispatchers.Main){
                Toast.makeText(requireContext(), "Register Berhasil", Toast.LENGTH_SHORT).show()
            }
        }catch (e: Exception){

        }
    }


    private fun isRequired(): Boolean{
        return (
            binding.edtInputName.text.toString().isNotEmpty() &&
            binding.edtInputEmail.text.toString().isNotEmpty() &&
            binding.edtInputPassword.text.toString().isNotEmpty() &&
            binding.edtInputPhoneNum.text.toString().isNotEmpty() &&
            binding.edtSelectRole.text.toString().isNotEmpty()
        )
    }


    companion object {
        private const val EMAIL = "email"
    }

}