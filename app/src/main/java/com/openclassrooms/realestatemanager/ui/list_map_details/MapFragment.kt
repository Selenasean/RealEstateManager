package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.utils.CurrencyCode
import com.openclassrooms.realestatemanager.utils.Utils

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        fun newInstance() = MapFragment()
    }

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMapBinding.inflate(layoutInflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMapBinding.bind(view)
        //get the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(binding.fragmentContainerMap.id) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


    }


    override fun onMapReady(map: GoogleMap) {
        viewModel.mapList.observe(viewLifecycleOwner, Observer { list ->
            map.clear()
            list.forEach { item ->
                if (item.latitude != null || item.longitude != null) {
                    //create marker for each realEstate
                    createMarkers(map, item)
                    zoomOnMap(map, item) //zoom on the latest real estate
                    //listener
                    map.setOnMarkerClickListener(this)
                } else {
                    Log.i("LatLng", "null : $item")
                }
            }


        })
    }

    private fun zoomOnMap(
        map: GoogleMap,
        item: MapState
    ) {
        //zoom near the latest real estate TODO : zoomer sur l'ensemble des marker
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    item.latitude!!,
                    item.longitude!!
                ), 10f
            )
        )
    }

    private fun createMarkers(
        map: GoogleMap,
        item: MapState
    ) {
        val realEstateMarker = map.addMarker(
            MarkerOptions()
                .position(LatLng(item.latitude!!, item.longitude!!))
                .title(ContextCompat.getString(requireContext(), item.type.displayName))
                .snippet(Utils.priceFormatter(item.priceTag, CurrencyCode.EURO))

        )
        //create data object to insert in the marker tag
        realEstateMarker?.tag = MarkerData(
            id = item.id,
            clickCount = 0
        )

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val markerData = marker.tag as? MarkerData
        val newClickCount: Int
        markerData?.clickCount?.let { clickCount ->
            when (clickCount) {
                0 -> {
                    markerData.clickCount = clickCount + 1
                    Log.i("clickCount", "normalement =1 car +1 : ${markerData.clickCount}")
                }

                1 -> {
                    viewModel.onRealEstateClick(markerData.id)
                    Log.i("clickCount", "normalement = 1 : $clickCount, id = ${markerData.id}")
                    markerData.clickCount = 0
                }

            }
        }
        return false
    }


}

data class MarkerData(
    val id: String,
    var clickCount: Int
)
