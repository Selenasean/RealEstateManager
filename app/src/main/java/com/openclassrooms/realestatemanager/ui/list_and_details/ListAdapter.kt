package com.openclassrooms.realestatemanager.ui.list_and_details


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import coil.load
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.RealestateItemBinding


class ListAdapter(private val clickedListener: (id: Long) -> Unit) :
    ListAdapter<ItemState, com.openclassrooms.realestatemanager.ui.list_and_details.ListAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemState>() {
            override fun areItemsTheSame(oldItem: ItemState, newItem: ItemState): Boolean {
                return oldItem.realEstate.id == newItem.realEstate.id
            }

            override fun areContentsTheSame(oldItem: ItemState, newItem: ItemState): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate the view of RealestateItemBinding and return ViewHolder object
        val binding: RealestateItemBinding = RealestateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //bind elements of the list with each items
        holder.bind(getItem(position))
    }

    //CLASS VIEW HOLDER
    inner class ViewHolder(private val binding: RealestateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemState: ItemState) {
            val realestate = itemState.realEstate
            val context = binding.root.context
            //set text
            binding.statusDisplayTv.text = ContextCompat.getString(context, realestate.status.state)
            if(realestate.status.state == R.string.for_sale){
                binding.statusDisplayTv.setTextColor(ContextCompat.getColor(context, R.color.md_theme_tertiaryFixed_mediumContrast))
            }else{
                binding.statusDisplayTv.setTextColor(ContextCompat.getColor(context, R.color.md_theme_error_mediumContrast))
            }
            binding.cityTv.text = realestate.city
            binding.buildingTypeTv.text = ContextCompat.getString(context, realestate.type.displayName)
            binding.priceTv.text = realestate.priceTag.toString().plus("€")
            if (realestate.photos.isNotEmpty()) {
                binding.imageRealestate.load(realestate.photos[0].urlPhoto) {
                    crossfade(true)
                }
                Log.i("listadapter", realestate.photos.toString())
            }
            if(itemState.isSelected){
                binding.realestateContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_primaryContainer))
            }else{
                binding.realestateContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.md_theme_background))
            }

        }

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                //check if bindingAdapterPosition is valid
                if(position != NO_POSITION){
                    clickedListener(getItem(position).realEstate.id)
                }
            }
        }

    }


}