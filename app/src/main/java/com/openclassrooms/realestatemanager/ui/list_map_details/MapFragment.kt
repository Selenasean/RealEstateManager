package com.openclassrooms.realestatemanager.ui.list_map_details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.openclassrooms.realestatemanager.utils.events.MapEvent
import com.openclassrooms.realestatemanager.utils.events.observeAsEvents
import kotlinx.coroutines.launch

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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMapBinding.bind(view)

        //get the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(binding.fragmentContainerMap.id) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
//        viewModel.getUserLocation()

    }


    /**
     * When map displayed ready,
     * Zoom map on user's location
     * Create marker for each realEstate in the list
     */
    override fun onMapReady(map: GoogleMap) {

        //zoom user's location
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }else{
            map.isMyLocationEnabled = true;
            zoomOnMap(map) //zoom on Paris, France
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.mapList.collect { list ->
                    map.clear()
                    list.forEach { item ->
                        //create a marker
                        createMarkers(map, item)
                        //listener
                        map.setOnMarkerClickListener(this@MapFragment)
                    }
                }
            }
        }
    }

    /**
     * Zoom on user's location
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun zoomOnMap(
        map: GoogleMap,
    ) {
        observeAsEvents(viewModel.eventMapFlow) { event ->
            when(event) {
                is MapEvent.CenterUserLocation ->  {
                    //zoom on map
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(event.location.latitude, event.location.longitude), 10f
                        )
                    )
                }
            }
        }
        // get location
        viewModel.onLocationPermission()
    }

    /**
     * To create markers on the map
     * And to custom them
     */
    private fun createMarkers(
        map: GoogleMap,
        item: MapState
    ) {
        val iconColor = if (item.status == Status.FOR_SALE) {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        } else {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
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

    /**
     * Count the clicks on marker to determine which action to do
     *
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        val markerData = marker.tag as? MarkerData
        markerData?.clickCount?.let { clickCount ->
            when (clickCount) {
                0 -> markerData.clickCount = clickCount + 1

                1 -> {
                    viewModel.onRealEstateClick(markerData.id)
                    markerData.clickCount = 0
                }

            }
        }
        return false
    }



}


//DATA CLASS
data class MarkerData(
    val id: String,
    var clickCount: Int
)
