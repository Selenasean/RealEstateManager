package com.openclassrooms.realestatemanager.ui.list_map_details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import coil.load
import com.openclassrooms.realestatemanager.databinding.ImageItemBinding
import com.openclassrooms.realestatemanager.ui.create_edit.CreateEditFragment


import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState


class PhotosAdapter(
    var className: String,
    private val labelClickedListener: (state: PhotoSelectedViewState) -> Unit,
    private val onDeleteClickedListener: (state : PhotoSelectedViewState) -> Unit
) :
    ListAdapter<PhotoSelectedViewState, PhotosAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PhotoSelectedViewState>() {
            override fun areItemsTheSame(oldItem: PhotoSelectedViewState, newItem: PhotoSelectedViewState): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PhotoSelectedViewState, newItem: PhotoSelectedViewState): Boolean {
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
        fun bind(photo: PhotoSelectedViewState) {
            //set photo
            Log.i("photoAdapter", "bind() ${photo.uri}")
            binding.imageItem.load(photo.uri) {
                crossfade(true)
            }
            if(photo.label.isEmpty()){
                binding.labelItem.visibility = View.GONE
            }else{
                binding.labelItem.text = photo.label

            }

            //set modification buttons
            val deleteBtn = binding.deleteImage
            val labelBtn = binding.labelCreate
            if (className == DetailFragment.CLASS_NAME) {
                deleteBtn.visibility = View.GONE
                labelBtn.visibility = View.GONE
            }
            if (className == CreateEditFragment.CLASS_NAME) {
                deleteBtn.visibility = View.VISIBLE
                labelBtn.visibility = View.VISIBLE

               //button to edit a label
                labelBtn.setOnClickListener {
                    val position = bindingAdapterPosition
                    //check if bindingAdapterPosition is valid
                    if (position != NO_POSITION) {
                        labelClickedListener(getItem(position))
                    }
                }

                //button to delete the photo selected
                deleteBtn.setOnClickListener {
                    val position = bindingAdapterPosition
                    //check if bindingAdapterPosition is valid
                    if (position != NO_POSITION) {
                        onDeleteClickedListener(getItem(position))
                    }
                }
            }


        }


    }
}