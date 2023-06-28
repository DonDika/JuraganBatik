package com.dondika.juraganbatik.ui.user.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.ItemRowUserCatalogBinding

class ListCatalogUserAdapter : RecyclerView.Adapter<ListCatalogUserAdapter.ListViewHolder>() {

    private val catalogs = ArrayList<Products>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowUserCatalogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(catalogs[position])
    }

    override fun getItemCount(): Int {
        return catalogs.size
    }

    fun setListCatalog(listCatalog: List<Products>){
        catalogs.clear()
        catalogs.addAll(listCatalog)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ListViewHolder(private val binding: ItemRowUserCatalogBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(products: Products){
            binding.apply {
                tvBatikName.text = products.batikName
                tvBatikPrice.text = "Rp ${products.batikPrice}"
                Glide.with(itemView)
                    .load(products.batikImg)
                    .into(batikImage)
                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(products)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(products: Products)
    }


}