package com.dondika.juraganbatik.ui.admin.transaction

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.data.model.Transaction
import com.dondika.juraganbatik.data.model.TransactionResponse
import com.dondika.juraganbatik.data.remote.ApiConfig
import com.dondika.juraganbatik.databinding.FragmentTransactionAdminBinding
import com.dondika.juraganbatik.ui.user.transaction.ListTransactionAdapter
import com.dondika.juraganbatik.utility.Utils
import com.dondika.juraganbatik.utility.constant.Firestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_detail_transaction.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TransactionAdminFragment : Fragment() {

    private var _binding: FragmentTransactionAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var listTransactionAdapter: ListTransactionAdapter
    private val transactionsCollectionRef = Firebase.firestore.collection(Firestore.TRANSACTIONS)
    private val pref by lazy { PreferenceManager(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setDataTransaction()
        setTotalAmount()
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun setListener() {
        binding.refreshData.setOnRefreshListener {
            //updateTransaction()
            setDataTransaction()
            setTotalAmount()
            binding.refreshData.isRefreshing = false
        }
    }

    private fun setAdapter() {
        listTransactionAdapter = ListTransactionAdapter()
        binding.rvTransaction.apply {
            adapter = listTransactionAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
        listTransactionAdapter.setOnItemClickCallback(object: ListTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(transaction: Transaction) {
                setupDialogDetailTransaction(transaction)
            }
        })
    }

    private fun setupDialogDetailTransaction(transaction: Transaction) {
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_detail_transaction, null)
        val myDialog = Dialog(requireActivity())
        myDialog.apply {
            setContentView(dialogBinding)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        dialogBinding.apply {
            tv_order.text = transaction.orderId
            tv_batik_name.text = transaction.batikName
            tv_amount_batik.text = transaction.amount
            tv_buyer_name.text = transaction.buyer
            tv_price_batik.text = Utils.amountFormat(transaction.batikPrice.toDouble().toInt())
            if (transaction.status == "settlement"){
                tv_status_payment.text = "Berhasil"
                tv_status_payment.setTextColor(Color.parseColor("#06DC86"))
            } else {
                tv_status_payment.text = "Pending"
                tv_status_payment.setTextColor(Color.parseColor("#DB4030"))
            }
        }

    }


    private fun setDataTransaction() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val user = pref.getString(Utils.PREF_EMAIL)
            val transactionList: ArrayList<Transaction> = arrayListOf()
            val listOfTransaction = transactionsCollectionRef
                .orderBy("transactionTime", Query.Direction.DESCENDING)
                .get()
                .await()
            for (document in listOfTransaction.documents){
                val listTransactions = Transaction(
                    transactionId = document.data!!["transactionId"].toString(), //document.reference.id,
                    orderId = document.data!!["orderId"].toString(),
                    batikName = document.data!!["batikName"].toString(),
                    batikPrice = document.data!!["batikPrice"].toString(),
                    amount = document.data!!["amount"].toString(),
                    buyer = document.data!!["buyer"].toString(),
                    sellerEmail = document.data!!["seller"].toString(),
                    sellerName = document.data!!["sellerName"].toString(),
                    status = document.data!!["status"].toString(),
                    transactionTime = document.data!!["transactionTime"].toString(),
                )
                Log.e("transaction", listTransactions.transactionId )
                if (listTransactions.sellerEmail == user){
                    transactionList.add(listTransactions)
                }
            }
            withContext(Dispatchers.Main){
                listTransactionAdapter.setListTransaction(transactionList)
            }

        } catch (e: Exception){

        }
    }

    private fun setTotalAmount() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var totalBalance = 0
            val listTransactionAmount = transactionsCollectionRef
                .whereEqualTo("seller", pref.getString(Utils.PREF_EMAIL))
                .whereEqualTo("status", "settlement")
                .get()
                .await()
            for (document in listTransactionAmount.documents){
                totalBalance += document.data!!["batikPrice"].toString().toDouble().toInt()
            }
            withContext(Dispatchers.Main){
                binding.tvSaldo.text = Utils.amountFormat(totalBalance)
            }

        }catch (e: Exception){

        }
    }

    private fun updateTransaction(){
        val transactionId = pref.getString(Utils.PREF_TRANSACTION_ID)
        if (transactionId?.isEmpty() == false){
            val client = ApiConfig.getApiService().getTransaction(transactionId)
            client.enqueue(object: Callback<TransactionResponse> {
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    if (response.isSuccessful){
                        val responseTransactionStatus = response.body()!!.transactionStatus
                        if (responseTransactionStatus == "settlement"){
                            //updateTransactionTest(transactionId)
                            pref.clearTransactionId()
                        }
                    }
                }
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }


    private fun updateTransactionTest(transactionId: String) = CoroutineScope(Dispatchers.IO).launch {
        val transactionQuery = transactionsCollectionRef
            .whereEqualTo("transactionId", "$transactionId")
            .get()
            .await()
        if (transactionQuery.documents.isNotEmpty()){
            for (document in transactionQuery){
                try {
                    transactionsCollectionRef
                        .document(document.id)
                        .update("status", "settlement")
                    setDataTransaction()
                }catch (e: Exception){

                }
            }
        }
    }


}