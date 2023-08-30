package com.dondika.juraganbatik.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.data.model.UserData
import com.dondika.juraganbatik.databinding.FragmentLoginBinding
import com.dondika.juraganbatik.ui.admin.MainActivityAdmin
import com.dondika.juraganbatik.ui.user.MainActivityUser
import com.dondika.juraganbatik.utility.Utils
import com.dondika.juraganbatik.utility.constant.Firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val usersCollectionRef = Firebase.firestore.collection(Firestore.USERS)
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupListener() {
        binding.loginButton.setOnClickListener {
            if (isRequired()){
                loginRequest()
            }
        }

        binding.registerButton.setOnClickListener {
            val registerFragment = RegisterFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, registerFragment, RegisterFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun loginRequest() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val checkUser = usersCollectionRef
                .whereEqualTo("email", binding.edtInputEmail.text.toString())
                .whereEqualTo("password", binding.edtInputPassword.text.toString())
                .get()
                .await()
            if (checkUser.isEmpty){
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "Periksa kembali email atau password anda!", Toast.LENGTH_SHORT).show()
                }
            } else {
                for (document in checkUser.documents){
                    val userData = UserData(
                        name = document.data!!["name"].toString(),
                        email = document.data!!["email"].toString(),
                        phoneNumber = document.data!!["phoneNumber"].toString(),
                        roles = document.data!!["roles"].toString(),
                        password = null
                    )
                    saveSession(userData)
                    if (userData.roles == Utils.PEMBELI){
                        startActivity(Intent(requireActivity(), MainActivityUser::class.java))
                        requireActivity().finish()
                    } else if (userData.roles == Utils.PENJUAL){
                        startActivity(Intent(requireActivity(), MainActivityAdmin::class.java))
                        requireActivity().finish()
                    }
                }

            }
        }catch (e:Exception){

        }

    }

    private fun saveSession(userData: UserData) {
        pref.putInt(Utils.PREF_IS_LOGIN, 1)
        pref.putString(Utils.PREF_NAME, userData.name)
        pref.putString(Utils.PREF_EMAIL, userData.email)
        pref.putString(Utils.PREF_PHONE_NUM, userData.phoneNumber)
        pref.putString(Utils.PREF_ROLES, userData.roles)
    }


    private fun isRequired(): Boolean{
        return (
            binding.edtInputEmail.text.toString().isNotEmpty() &&
            binding.edtInputPassword.text.toString().isNotEmpty()
        )
    }



}