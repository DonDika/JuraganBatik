package com.dondika.juraganbatik.ui.admin.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.data.model.Products
import com.dondika.juraganbatik.databinding.ItemRowAdminCatalogBinding

class ListCatalogAdapter :
    RecyclerView.Adapter<ListCatalogAdapter.ListViewHolder>(){

    private val catalogs = ArrayList<Products>()

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


    fun setListCatalog(listCatalog: List<Products>){
        catalogs.clear()
        catalogs.addAll(listCatalog)
        notifyDataSetChanged()
        Log.e("retrieveToAdapter", listCatalog.toString() )
    }


    class ListViewHolder(private val binding: ItemRowAdminCatalogBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(listCatalog: Products){
            binding.apply {
                //Log.e("retrieveToBind", listCatalog.toString() )
                //Bind data
                tvBatikName.text = listCatalog.batikName
                tvBatikPrice.text = "Rp ${listCatalog.batikPrice}"
                Glide.with(itemView)
                    .load(listCatalog.batikImg)
                    .into(batikImage)
            }
        }

    }


}