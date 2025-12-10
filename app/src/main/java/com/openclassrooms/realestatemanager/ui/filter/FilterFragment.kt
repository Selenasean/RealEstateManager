package com.openclassrooms.realestatemanager.ui.filter

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.databinding.FragmentFilterBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import kotlinx.coroutines.launch

class FilterFragment: BottomSheetDialogFragment(R.layout.fragment_filter) {

    companion object{
        const val TAG = "FILTER_BOTTOM_SHEET"
    }


    private val viewModel by viewModels<FilterViewModel>{ ViewModelFactory.Companion.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFilterBinding.bind(view)
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {
                    render(it, binding)
                }
            }
        }

        //btn apply filter listener
        binding.applyFilterBtn.setOnClickListener {
            viewModel.applyFilters()
            dismiss()
        }
        //btn clear filter listener
        binding.clearFilterBtn.setOnClickListener {
            viewModel.clearFilters()
            dismiss()
        }

        //listeners
        binding.tvCity.doAfterTextChanged {  viewModel.updateCity(it.toString()) }
        binding.tvPricemin.doAfterTextChanged { viewModel.updatePriceMin(it.toString()) }
        binding.tvPricemax.doAfterTextChanged { viewModel.updatePriceMax(it.toString()) }
        binding.tvSurfacemin.doAfterTextChanged { viewModel.updateSurfaceMin(it.toString()) }
        binding.tvSurfacemax.doAfterTextChanged { viewModel.updateSurfaceMax(it.toString()) }
        binding.chipSold.setOnCheckedChangeListener { _ , isChecked ->
            if(isChecked){
                viewModel.updateStatus(Status.SOLD)
            }else{
                viewModel.updateStatus(null)
            }
        }
        binding.chipForSale.setOnCheckedChangeListener { _ , isChecked ->
            if(isChecked){
                viewModel.updateStatus(Status.FOR_SALE)
            }else{
                viewModel.updateStatus(null)
            }
        }

    }

    private fun render(
        it: FilterState,
        binding: FragmentFilterBinding
    ) {
        //city
        if(binding.tvCity.text.toString() != it.city && it.city != null){
            viewModel.updateCity(it.city)
            binding.tvCity.setText(it.city)
        }
        //type
         renderTypeChips(binding, it.type)

        //price
        if(binding.tvPricemin.text.toString() != it.priceMin.toString() && it.priceMin != null){
            viewModel.updatePriceMin(it.priceMin.toString())
            binding.tvPricemin.setText(it.priceMin.toString())
        }
        if(binding.tvPricemax.text.toString() != it.priceMax.toString() && it.priceMax != null){
            viewModel.updatePriceMax(it.priceMax.toString())
            binding.tvPricemax.setText(it.priceMax.toString())
        }
        //surface
        if(binding.tvSurfacemin.text.toString() != it.surfaceMin.toString() && it.surfaceMin != null){
            viewModel.updateSurfaceMin(it.surfaceMin.toString())
            binding.tvSurfacemin.setText(it.surfaceMin.toString())
        }
        if(binding.tvSurfacemax.text.toString() != it.surfaceMax.toString() && it.surfaceMax != null){
            viewModel.updateSurfaceMax(it.surfaceMax.toString())
            binding.tvSurfacemax.setText(it.surfaceMax.toString())
        }
        //status
            renderStatusChips(binding, it.status)
    }


    private fun renderStatusChips(binding: FragmentFilterBinding, statusSelected: Status?) {
        if(statusSelected != null){
            binding.chipSold.isChecked = statusSelected == Status.SOLD
            binding.chipForSale.isChecked = statusSelected == Status.FOR_SALE
        }

    }

    private fun renderTypeChips(binding: FragmentFilterBinding, typeList: List<BuildingType>) {
        val chipGroup = binding.chipGroupType
        chipGroup.removeAllViews()

        BuildingType.entries.forEach { type ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.chip_layout, chipGroup, false) as Chip
            chip.text = type.name

            chipGroup.addView(chip)
            chip.isChecked = typeList.contains(type)

            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateTypeSelected(type, isChecked)
            }
        }

    }


}