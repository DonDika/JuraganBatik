package com.dondika.juraganbatik.ui.user.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.data.model.Transaction
import com.dondika.juraganbatik.databinding.ItemRowTransactionHistoryBinding
import com.dondika.juraganbatik.utility.Utils

class ListTransactionAdapter : RecyclerView.Adapter<ListTransactionAdapter.ListViewHolder>() {

    private val transactions = ArrayList<Transaction>()
    private lateinit var onItemClickCallback: OnItemClickCallback


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowTransactionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setListTransaction(transaction: List<Transaction>){
        transactions.clear()
        transactions.addAll(transaction)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(private val binding: ItemRowTransactionHistoryBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: Transaction){
            binding.apply {
                tvBatikName.text = transaction.batikName
                tvBatikPrice.text = Utils.amountFormat(transaction.batikPrice.toDouble().toInt())
                if (transaction.status == "settlement"){
                    tvStatus.text = "Berhasil"
                    tvStatus.setTextColor(Color.parseColor("#06DC86"))
                } else {
                    tvStatus.text = "Pending"
                    tvStatus.setTextColor(Color.parseColor("#DB4030"))
                }
                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(transaction)
                }
            }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(transaction: Transaction)
    }


}