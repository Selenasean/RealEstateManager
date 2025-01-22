package com.openclassrooms.realestatemanager.ui.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.databinding.FragmentCreateBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.list_and_details.PhotosAdapter
import java.io.File
import kotlin.random.Random

class CreateFragment : BottomSheetDialogFragment(R.layout.fragment_create) {

    companion object {
        fun newInstance() = CreateFragment()
        const val TAG = "CREATE_BOTTOM_SHEET"
        const val CLASS_NAME = "CREATE_FRAGMENT"
    }

    private val viewModel by activityViewModels<CreateViewModel> { ViewModelFactory.getInstance() }
    private lateinit var adapter: PhotosAdapter
    private var currentPhotoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateBinding.bind(view)
        val context = binding.root.context

        //TODO : photos to add and display : listener to open photopicker and choose photo + label ?
        // TODO : onclick on picture from rv choose to change picture and label ?

        //settings for dropDown menus, chips, viewModel & recyclerView
        dropDownMenusSettings(binding, context)
        displayAmenitiesChips(binding, context)
        viewModel.state.observe(viewLifecycleOwner) { render(it, binding) }
        binding.createBtn.setOnClickListener { viewModel.createRealEstate() }
        setRecyclerView(binding)

        //inputs listeners
        binding.tvAddress.doAfterTextChanged { viewModel.updateAddress(it.toString()) }
        binding.tvCity.doAfterTextChanged { viewModel.updateCity(it.toString()) }
        binding.tvPrice.doAfterTextChanged { viewModel.updatePrice(it.toString()) }
        binding.tvSurface.doAfterTextChanged { viewModel.updateSurface(it.toString()) }
        binding.tvRooms.doAfterTextChanged { viewModel.updateRooms(it.toString()) }
        binding.tvBedrooms.doAfterTextChanged { viewModel.updateBedrooms(it.toString()) }
        binding.tvBathrooms.doAfterTextChanged { viewModel.updateBathrooms(it.toString()) }
        binding.tvDescription.doAfterTextChanged { viewModel.updateDescription(it.toString()) }

        //photopicker media & listener
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                if (uris.isNotEmpty()) {
                    Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                    Log.d("PhotoPicker", "uris = ${uris}")
                    viewModel.updatePhotos(uris)
                }
            }
        binding.selectPictureBtn.setOnClickListener {
            openPhotoPicker(pickMedia)
        }
        //take picture from device
        val takePictureCallback =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { successful ->
                if (successful) {
                    //do something

                }

            }

        binding.takePictureBtn.setOnClickListener {
            currentPhotoUri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                File.createTempFile(
                    "JPEG_",
                    ".jpg",
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
            )

            takePictureCallback.launch(currentPhotoUri)
        }

    }

    private fun setRecyclerView(binding: FragmentCreateBinding) {
        val recyclerView = binding.rvPhotosSelected
        adapter = PhotosAdapter(CLASS_NAME, labelClickedListener = { state: PhotoSelectedViewState ->
            //open a dialog with edit text and display photo
            val showDialog = CreateLabelDialogFragment.newInstance(state)
            showDialog.show(childFragmentManager, "dialog")
        })
        recyclerView.adapter = adapter
    }

    private fun openPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun render(it: RealEstateCreatedState, binding: FragmentCreateBinding) {
        binding.createBtn.isEnabled = it.isCreatedEnabled()
        if (it.photos.isEmpty()) {
            binding.rvPhotosSelected.visibility = View.GONE
        } else {
            binding.rvPhotosSelected.visibility = View.VISIBLE
            adapter.submitList(it.photos)
        }


    }

    private fun displayAmenitiesChips(binding: FragmentCreateBinding, context: Context) {
        val chipsGroup = binding.chipGroup

        Amenity.entries.forEach { amenity ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.chip_layout, chipsGroup, false) as Chip
            chip.text = ContextCompat.getString(context, amenity.displayName)

            //attributes id to each chip
            chip.id = Random.nextInt()
            chipsGroup.addView(chip)

            //listener
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateAmenities(amenity, isChecked)
            }
        }

    }

    private fun dropDownMenusSettings(binding: FragmentCreateBinding, context: Context) {
        //for type of real estate menu
        val typeItems =
            BuildingType.entries.map { ContextCompat.getString(context, it.displayName) }
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_create, typeItems)
        binding.tvType.setAdapter(typeAdapter)

        //listener
        binding.tvType.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateType(BuildingType.entries[position])
        }

        //for agent's name menu
        viewModel.getAgents().observe(viewLifecycleOwner) { agents ->
            val agentAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_create,
                agents.map { agent -> agent.name }
            )
            binding.tvAgent.setAdapter(agentAdapter)

            //listener
            binding.tvAgent.setOnItemClickListener { _, _, position, _ ->
                viewModel.updateAgentName(agents[position])
            }
        }

    }

}
