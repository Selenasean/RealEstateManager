package com.openclassrooms.realestatemanager.ui


import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.openclassrooms.realestatemanager.databinding.RealestateItemBinding
import com.openclassrooms.realestatemanager.domain.RealEstate


class MainAdapter(private val clickedListener: (id: String) -> Unit) :
    ListAdapter<RealEstate, MainAdapter.ViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RealEstate>() {
            override fun areItemsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RealEstate, newItem: RealEstate): Boolean {
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
        fun bind(realEstate: RealEstate) {
            //set text
            binding.nameTv.text = realEstate.title
        }

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                //check if bindingAdapterPosition is valid
                if(position != NO_POSITION){
                    clickedListener(getItem(position).id)
                }
            }
        }

    }


}