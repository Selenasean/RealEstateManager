package com.openclassrooms.realestatemanager.ui.list_and_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.ui.ViewModelFactory

class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val viewModel by activityViewModels<ListDetailViewModel> { ViewModelFactory.getInstance() }
    private lateinit var adapter: PhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDetailBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailBinding.bind(view)
        setRecyclerView(binding)
        binding.noItemLytContainer.visibility = View.GONE
        viewModel.detailState.observe(viewLifecycleOwner) { render(it, binding) }
    }

    private fun render(realEstate: RealEstate?, binding: FragmentDetailBinding) {
        if (realEstate == null) {
            binding.noItemLytContainer.visibility = View.VISIBLE
            binding.constraintlayoutContainer.visibility = View.GONE
        } else {
            binding.constraintlayoutContainer.visibility = View.VISIBLE
            binding.noItemLytContainer.visibility = View.GONE
            Log.i("detailFragment", realEstate.photos.size.toString())
            adapter.submitList(realEstate.photos)
            binding.cityTv.text = realEstate.city
            binding.lytAttributes.surfaceValueTv.text = realEstate.surface.toString()
            binding.lytAttributes.roomsValueTv.text = realEstate.rooms.toString()
            binding.lytAttributes.bedroomsValueTv.text = realEstate.bedrooms.toString()
            binding.lytAttributes.bathroomsValueTv.text = realEstate.bathrooms.toString()
            binding.descriptionContent.text = realEstate.description
            binding.locationValueTv.text = realEstate.address
        }

    }

    private fun setRecyclerView(binding: FragmentDetailBinding) {
        val recyclerView = binding.imagesRv
        adapter = PhotosAdapter()
        recyclerView.adapter = adapter
    }


}