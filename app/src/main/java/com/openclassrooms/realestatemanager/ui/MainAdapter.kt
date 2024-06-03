package com.openclassrooms.realestatemanager.ui


import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.RealestateItemBinding
import com.openclassrooms.realestatemanager.domain.RealEstate

class MainAdapter(private val dataSet: List<RealEstate>) :RecyclerView.Adapter<MainAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        //inflate the view of RealestateItemBinding and return ViewHolder object
        val binding: RealestateItemBinding = RealestateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        //bind elements of the list with each items
        holder.bind(dataSet[position])
    }

    //return size of our dataSet
    override fun getItemCount(): Int = dataSet.size


    //CLASS VIEW HOLDER
    class ViewHolder(private val binding: RealestateItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(realEstate: RealEstate){
            //set text
            binding.nameTv.text = realEstate.title
        }
    }

}