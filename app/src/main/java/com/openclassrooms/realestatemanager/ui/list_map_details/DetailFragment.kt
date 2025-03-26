package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.Position
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.utils.CurrencyCode
import com.openclassrooms.realestatemanager.utils.Utils

class DetailFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = DetailFragment()
        const val CLASS_NAME = "DETAIL_FRAGMENT"
    }

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }
    private lateinit var adapter: PhotosAdapter
    private lateinit var realEstatePosition: Position

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

        //map view dsplayed
        val mapView = binding.mapSnapshot
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }
    }

    /**
     * To render elements on UI
     */
    private fun render(realEstate: RealEstateDetailViewState?, binding: FragmentDetailBinding) {
        if (realEstate == null) {
            binding.noItemLytContainer.visibility = View.VISIBLE
            binding.constraintlayoutContainer.visibility = View.GONE
        } else {
            val context = binding.root.context
            binding.constraintlayoutContainer.visibility = View.VISIBLE
            binding.noItemLytContainer.visibility = View.GONE
            adapter.submitList(realEstate.photos)
            binding.descriptionContent.text = realEstate.description
            binding.lytAttributes.statusValueTv.text =
                ContextCompat.getString(context, realEstate.status.state)
            if (realEstate.status.state == R.string.for_sale) {
                binding.lytAttributes.statusValueTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_tertiaryFixed_mediumContrast
                    )
                )
            } else {
                binding.lytAttributes.statusValueTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_error_mediumContrast
                    )
                )
            }
            binding.lytAttributes.priceValueTv.text =
                Utils.priceFormatter(realEstate.priceTag, CurrencyCode.EURO)
            binding.lytLocationAmenities.cityTv.text = realEstate.city
            binding.lytAttributes.surfaceValueTv.text = realEstate.surface.toString()
            binding.lytAttributes.roomsValueTv.text = realEstate.rooms.toString()
            binding.lytAttributes.bedroomsValueTv.text = realEstate.bedrooms.toString()
            binding.lytAttributes.bathroomsValueTv.text = realEstate.bathrooms.toString()
            binding.lytLocationAmenities.locationValueTv.text = realEstate.address
            val amenitiesString =
                realEstate.amenities.map { ContextCompat.getString(context, it.displayName) }
            binding.lytLocationAmenities.amenitiesValueTv.text = amenitiesString.joinToString(", ")
            realEstatePosition= Position(realEstate.latitude!!, realEstate.longitude!!)
        }

    }

    private fun setRecyclerView(binding: FragmentDetailBinding) {
        val recyclerView = binding.imagesRv
        //comment ne rien faire
        adapter =
            PhotosAdapter(CLASS_NAME, labelClickedListener = { Unit }, onDeleteClickedListener = {})
        recyclerView.adapter = adapter
    }

    override fun onMapReady(map: GoogleMap) {
        if(true){
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(realEstatePosition.latitude,
                        realEstatePosition.longitude), 13f
                )
            )
            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        realEstatePosition.latitude,
                        realEstatePosition.longitude
                    )
                ))
        }

    }





}