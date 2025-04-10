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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.data.model.Status
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
        zoomOnMap(map) //zoom on Paris, France
        viewModel.mapList.observe(viewLifecycleOwner, Observer { list ->
            map.clear()
            list.forEach { item ->

                //create marker for each realEstate
                createMarkers(map, item)

                //listener
                map.setOnMarkerClickListener(this)

            }


        })
    }

    private fun zoomOnMap(
        map: GoogleMap,
    ) {
        val parisGPS = LatLng(48.864716, 2.333333)
        //zoom on Paris, France
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                parisGPS, 10f
            )
        )
    }

    private fun createMarkers(
        map: GoogleMap,
        item: MapState
    ) {
        val iconColor = if (item.status == Status.FOR_SALE) {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        } else {
            BitmapDescriptorFactory.defaultMarker()
        }

        val realEstateMarker = map.addMarker(
            MarkerOptions()
                .icon(iconColor)
                .position(LatLng(item.latitude, item.longitude))
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
