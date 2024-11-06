package com.openclassrooms.realestatemanager.ui.create

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.databinding.FragmentCreateBinding
import com.openclassrooms.realestatemanager.domain.RealEstateAgent
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import kotlin.random.Random

class CreateFragment : BottomSheetDialogFragment(R.layout.fragment_create) {

    companion object{
        fun newInstance() = CreateFragment()
        const val TAG = "CREATE_BOTTOM_SHEET"
    }

    private val viewModel by activityViewModels<CreateViewModel> { ViewModelFactory.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateBinding.bind(view)
        val context = binding.root.context

        //settings for dropDown menus & chips
        dropDownMenusSettings(binding, context)
        displayAmenitiesChips(binding,context)
        viewModel.state.observe( viewLifecycleOwner){ render(it, binding) }
        binding.createBtn.setOnClickListener { view -> createRealEstate() }

    }

    private fun render(it: RealEstateCreatedState, binding: FragmentCreateBinding) {
        binding.createBtn.isEnabled = it.isCreatedEnabled()
    }


    private fun createRealEstate() {
        //TODO: create realEstate : type, address, city, price, surface, rooms, bedrooms, bathrooms, description, amenities,agentName
        var type: String
        var adress: String
        var city: String
        var price: Int
        var surface: Int
        var rooms: Int
        var bedrooms: Int
        var batrooms: Int
        var descripton: String
        var amenities: List<String>
        var agentName: String

    }

    private fun displayAmenitiesChips(binding: FragmentCreateBinding, context: Context) {
        val chipsGroup = binding.chipGroup
        val amenitiesList: List<String> = Amenity.entries.map { ContextCompat.getString(context,it.displayName) }

        amenitiesList.forEach{ amenity ->
            val chip = LayoutInflater.from(context).inflate(R.layout.chip_layout,chipsGroup,false) as Chip
            chip.text = amenity
            //attributes id to each chip
            chip.id = Random.nextInt()
            chipsGroup.addView(chip)
        }


    }

    private fun dropDownMenusSettings(binding: FragmentCreateBinding,context: Context) {
        //for type of real estate menu
        val typeItems = BuildingType.entries.map { ContextCompat.getString(context,it.displayName) }
        val typeAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_menu_create, typeItems)
        binding.tvType.setAdapter(typeAdapter)

        //for agent's name menu
        viewModel.getAgentsName().observe(viewLifecycleOwner){
            val agentAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_create,it)
            binding.tvAgent.setAdapter(agentAdapter)
        }



    }
    


}
