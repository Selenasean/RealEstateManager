package com.openclassrooms.realestatemanager.ui.list_and_details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.openclassrooms.realestatemanager.databinding.ImageItemBinding
import com.openclassrooms.realestatemanager.domain.Photo
import com.openclassrooms.realestatemanager.ui.create.CreateFragment


class PhotosAdapter(var className: String) : ListAdapter<Photo, PhotosAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ImageItemBinding = ImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    //CLASS VIEW HOLDER
    inner class ViewHolder(private val binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            //set photo
            Log.i("photoAdapter", "bind() ${photo.urlPhoto}" )
            binding.imageItem.
            load(photo.urlPhoto) {
                crossfade(true)
            }
            binding.labelItem.text = photo.label
            //set modification buttons
            val deleteBtn = binding.deleteImage
            val labelBtn = binding.labelCreate
            if(className == DetailFragment.CLASS_NAME){
                deleteBtn.visibility = View.GONE
               labelBtn.visibility = View.GONE
            }
            if(className == CreateFragment.CLASS_NAME){
                deleteBtn.visibility = View.VISIBLE
                labelBtn.visibility = View.VISIBLE

                //TODO : deleteBtn renvoi vers la suppression de la liste de la photo supp
                //TODO : labelBtn ouvre un dialog pour ecrire un label (caractère limité)
            }
        }
       
    }
}