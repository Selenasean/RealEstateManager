package com.openclassrooms.realestatemanager.ui.create

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentCreateLabelDialogBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState

// the fragment initialization parameters
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateLabelDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateLabelDialogFragment() : DialogFragment() {

    private val viewModel by activityViewModels<CreateViewModel> { ViewModelFactory.getInstance() }

    companion object{
        fun newInstance(state: PhotoSelectedViewState): CreateLabelDialogFragment{
            return CreateLabelDialogFragment().apply { arguments = bundleOf(ARG_PARAM1 to state) }
        }
    }

    @SuppressLint("PrivateResource")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentCreateLabelDialogBinding.inflate(layoutInflater)
        val state = BundleCompat.getParcelable(requireArguments(), ARG_PARAM1,PhotoSelectedViewState::class.java)!!
        binding.photo.load(state.uri)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.add_label))
            .setView(binding.root)
            .setPositiveButton(R.string.create_label) { dialog, _ ->
                viewModel.updateLabel(binding.tvLabel.text?.toString() ?: "", state.id)
            }
            .create()
    }


}