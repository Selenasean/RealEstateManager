package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import com.openclassrooms.realestatemanager.ui.create_edit.CreateFragment
import com.openclassrooms.realestatemanager.utils.CurrencyCode
import com.openclassrooms.realestatemanager.utils.Utils

class DetailFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = DetailFragment()
        const val CLASS_NAME = "DETAIL_FRAGMENT"
    }

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }
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
        viewModel.detailState.observe(viewLifecycleOwner) {
            render(
                it,
                binding,

            )
        }

        //TODO :listener on FAB update + get realestate id into intent
        binding.fabUpdate.setOnClickListener {
            val id = viewModel.detailState.value?.id
            if (id!= null) {
                val createBottomSheet = CreateFragment.newInstance(
                    id = id
                )
                val fragmentManager = (activity as FragmentActivity).supportFragmentManager
                fragmentManager.let { createBottomSheet.show(it, CreateFragment.TAG) }
            }


        }

        //map view displayed
        val mapView = binding.mapSnapshot
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    /**
     * To render elements on UI
     */
    private fun render(
        realEstate: RealEstateDetailViewState?,
        binding: FragmentDetailBinding,

    ) {
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
                Utils.priceFormatter(price = realEstate.priceTag, CurrencyCode.EURO)
            binding.lytLocationAmenities.cityTv.text = realEstate.city
            binding.lytAttributes.surfaceValueTv.text = realEstate.surface.toString()
            binding.lytAttributes.roomsValueTv.text = realEstate.rooms.toString()
            binding.lytAttributes.bedroomsValueTv.text = realEstate.bedrooms.toString()
            binding.lytAttributes.bathroomsValueTv.text = realEstate.bathrooms.toString()
            binding.lytLocationAmenities.locationValueTv.text = realEstate.address
            val amenitiesString =
                realEstate.amenities.map { ContextCompat.getString(context, it.displayName) }
            binding.lytLocationAmenities.amenitiesValueTv.text = amenitiesString.joinToString(", ")
            viewModel.realEstatePosition(
                Position(
                    latitude = realEstate.latitude!!,
                    longitude = realEstate.longitude!!
                )
            )
        }


    }

    /**
     * RV settings
     */
    private fun setRecyclerView(binding: FragmentDetailBinding) {
        val recyclerView = binding.imagesRv
        adapter =
            PhotosAdapter(CLASS_NAME, labelClickedListener = {}, onDeleteClickedListener = {})
        recyclerView.adapter = adapter
    }

    /**
     * onMapReady callback
     */
    override fun onMapReady(map: GoogleMap) {
        viewModel.positionStateFlow.observe(viewLifecycleOwner) {
            if (it != null) {
                renderMap(map, it)
            }
        }
    }

    /**
     * Displaying markers on map according real estate's position
     */
    private fun renderMap(map: GoogleMap, position: Position) {
        map.clear()
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    position.latitude,
                    position.longitude
                ), 10f
            )
        )
        map.addMarker(
            MarkerOptions().position(
                LatLng(
                    position.latitude,
                    position.longitude
                )
            )
        )
    }

}


