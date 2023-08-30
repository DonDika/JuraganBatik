package com.dondika.juraganbatik.ui.admin.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.databinding.ItemRowAdminCatalogBinding
import com.dondika.juraganbatik.utility.Utils

class ListCatalogAdapter :
    RecyclerView.Adapter<ListCatalogAdapter.ListViewHolder>(){

    private val catalogs = ArrayList<ProductsResponse>()
    //private lateinit var onItemClick: OnItemClickCallback
    private lateinit var onItemClick: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowAdminCatalogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(catalogs[position])
    }

    override fun getItemCount(): Int {
        return catalogs.size
    }


    fun setListCatalog(listCatalog: List<ProductsResponse>){
        catalogs.clear()
        catalogs.addAll(listCatalog)
        notifyDataSetChanged()
        //Log.e("retrieveToAdapter", listCatalog.toString() )
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClick = onItemClickCallback
    }


    inner class ListViewHolder(private val binding: ItemRowAdminCatalogBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(listCatalog: ProductsResponse){
            binding.apply {
                //Log.e("retrieveToBind", listCatalog.toString() )
                //Bind data
                tvBatikName.text = listCatalog.batikName
                tvBatikPrice.text = Utils.amountFormat(listCatalog.batikPrice.toInt())
                Glide.with(itemView)
                    .load(listCatalog.batikImg)
                    .into(batikImage)
                deleteButton.setOnClickListener {
                    onItemClick.onItemDeleted(listCatalog)
                }
                root.setOnClickListener {
                    onItemClick.onItemClicked(listCatalog)
                }
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(products: ProductsResponse)
        fun onItemDeleted(products: ProductsResponse)
    }

}