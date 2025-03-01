package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory

class MapFragment: Fragment(), OnMapReadyCallback {

    companion object{
        fun newInstance() = MapFragment()
    }

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMapBinding.inflate(layoutInflater,container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMapBinding.bind(view)
        //get the SupportMapFragment and get notified when the is ready t be used.
        val mapFragment = childFragmentManager.findFragmentById(binding.fragmentContainerMap.id) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }


    override fun onMapReady(p0: GoogleMap) {
        Log.i("mapFragment", "map ready")

    }


}