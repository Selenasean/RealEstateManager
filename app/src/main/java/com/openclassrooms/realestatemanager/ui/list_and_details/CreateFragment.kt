package com.openclassrooms.realestatemanager.ui.list_and_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentCreateBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory

class CreateFragment : BottomSheetDialogFragment(R.layout.fragment_create) {

    companion object{
        fun newInstance() = CreateFragment()
        const val TAG = "CREATE_BOTTOM_SHEET"
    }

    private val viewModel by activityViewModels<CreateViewModel> { ViewModelFactory.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateBinding.bind(view)
    }


}