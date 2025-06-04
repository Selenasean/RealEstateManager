package com.openclassrooms.realestatemanager.ui.create_edit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.databinding.FragmentCreateBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.list_map_details.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.events.CreationEvents
import com.openclassrooms.realestatemanager.utils.observeAsEvents
import kotlinx.serialization.StringFormat
import java.io.File
import kotlin.random.Random

class CreateEditFragment : BottomSheetDialogFragment(R.layout.fragment_create) {

    companion object {
        const val TAG = "CREATE_BOTTOM_SHEET"
        const val CLASS_NAME = "CREATE_FRAGMENT"
        const val REAL_ESTATE_ID = "REAL_ESTATE_ID"

        fun newInstance(id: String?) = CreateEditFragment().apply{
            //put id in argument within bundle
            arguments = bundleOf( REAL_ESTATE_ID to id)
        }

    }

    private val viewModel by viewModels<CreateEditViewModel> { ViewModelFactory.Companion.getInstance() }
    private lateinit var adapter: PhotosAdapter
    private var currentPhotoUri: Uri? = null
    private lateinit var typeAdapter : ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateBinding.bind(view)
        val context = binding.root.context

        typeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_create,
            BuildingType.entries.map { ContextCompat.getString(context, it.displayName) }
        )

        //settings for dropDown menus, chips, viewModel & recyclerView
        dropDownMenusSettings(binding, context)
        displayAmenitiesChips(binding, context)
        viewModel.state.observe(viewLifecycleOwner) { render(it, binding, context) }
        setRecyclerView(binding)

        //create a realEstate
        binding.createBtn.setOnClickListener {
            if(viewModel.isPositionExist()){
                viewModel.saveRealEstate()
                Log.i("CreateFragment", "onViewCreated:  create RealEstate")
                binding.textInputLytAddress.helperText = null
            }else{
                binding.textInputLytAddress.helperText =
                    ContextCompat.getString(context, R.string.address_invalid)
            }

        }
        //observe events to close creation dialog after real estate's creation succeed
        //or to notify if there is internet
        observeAsEvents(viewModel.isCreatedFlow) { event ->
            when (event) {
                CreationEvents.isCreated -> {
                    Toast.makeText(requireContext(), R.string.creation_succeed, Toast.LENGTH_SHORT).show()
                    dismiss()
                }

                CreationEvents.isInternetAvailable -> Toast.makeText(requireContext(), R.string.no_internet,
                    Toast.LENGTH_SHORT).show()
            }
        }



        //inputs listeners

        binding.tvAddress.doAfterTextChanged {
                viewModel.updateAddress(it.toString())

        }
        binding.tvCity.doAfterTextChanged { viewModel.updateCity(it.toString()) }
        binding.tvPrice.doAfterTextChanged { viewModel.updatePrice(it.toString()) }
        binding.tvSurface.doAfterTextChanged { viewModel.updateSurface(it.toString()) }
        binding.tvRooms.doAfterTextChanged { viewModel.updateRooms(it.toString()) }
        binding.tvBedrooms.doAfterTextChanged { viewModel.updateBedrooms(it.toString()) }
        binding.tvBathrooms.doAfterTextChanged { viewModel.updateBathrooms(it.toString()) }
        binding.tvDescription.doAfterTextChanged { viewModel.updateDescription(it.toString()) }

        //photo picker media & listener
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                if (uris.isNotEmpty()) {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    uris.forEach { uri ->
                        context.contentResolver.takePersistableUriPermission(uri, flag)
                    }

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
                    viewModel.addPictureTaken(currentPhotoUri.toString())
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


    /**
     * Display recyclerView of photos or not depending on the size of the photo's list
     */
    private fun render(it: RealEstateToSaveState, binding: FragmentCreateBinding, context: Context) {
        if(it.isUpdated) binding.titleTv.text = getString(R.string.update_title)
        binding.createBtn.isEnabled = it.isInputsCompleted()
        //render type

        if(it.type != null && binding.tvType.text.toString() != ContextCompat.getString(context, it.type.displayName)){
            binding.tvType.setText(ContextCompat.getString(context, it.type.displayName))
            //TODO : obliger de reconstruire mon adapter ?
            typeAdapterBuilder(binding.tvType)
        }
        //render photo
        if (it.photos.isEmpty()) {
            binding.rvPhotosSelected.visibility = View.GONE
        } else {
            binding.rvPhotosSelected.visibility = View.VISIBLE
            adapter.submitList(it.photos)
        }
        //render address, city, price, surface, rooms, bedrooms, bathrooms, description
        if( binding.tvAddress.text.toString() != it.address){
            binding.tvAddress.setText(it.address)
        }
        if(binding.tvCity.text.toString() != it.city){
            binding.tvCity.setText(it.city)
        }
        //render price
        if(binding.tvPrice.text.toString() != it.price){
            binding.tvPrice.setText(it.price)
        }
        //render surface
        if(binding.tvSurface.text.toString() != it.surface){
            binding.tvSurface.setText(it.surface)
        }
        //render rooms
        if(binding.tvRooms.text.toString() != it.rooms){
            binding.tvRooms.setText(it.rooms)
        }
        //render bedrooms
        if(binding.tvBedrooms.text.toString() != it.bedrooms){
            binding.tvBedrooms.setText(it.bedrooms)
        }
        //render bathrooms
        if (binding.tvBathrooms.text.toString() != it.bathrooms){
            binding.tvBathrooms.setText(it.bathrooms)
        }
        //render description
        if(binding.tvDescription.text.toString() != it.description){
            binding.tvDescription.setText(it.description)
        }
        //render amenities
        //TODO : maj amenities
        //render agent's name
        if(it.agent != null && binding.tvAgent.text.toString() != it.agent.name){
            binding.tvAgent.setText(it.agent.name)
        }
    }


    /**
     * Settings for recycler view used to display photo taken or selected by user
     */
    private fun setRecyclerView(binding: FragmentCreateBinding) {
        val recyclerView = binding.rvPhotosSelected
        adapter =
            PhotosAdapter(CLASS_NAME, labelClickedListener = { state: PhotoSelectedViewState ->
                //open a dialog with edit text and display photo to edit a label
                val showDialog = CreateLabelDialogFragment.newInstance(state)
                showDialog.show(childFragmentManager, "dialog")
            }, onDeleteClickedListener = { state: PhotoSelectedViewState ->
                viewModel.deletePhoto(state.id)
            })
        recyclerView.adapter = adapter
    }

    /**
     * To open the PhotoPicker
     */
    private fun openPhotoPicker(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * To display amenities chips and listener
     */
    private fun displayAmenitiesChips(binding: FragmentCreateBinding, context: Context) {
        val chipsGroup = binding.chipGroup

        Amenity.entries.forEach { amenity ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.chip_layout, chipsGroup, false) as Chip
            chip.text = ContextCompat.getString(context, amenity.displayName)

            //attributes id to each chip
            chip.id = Random.Default.nextInt()
            chipsGroup.addView(chip)

            //listener
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateAmenities(amenity, isChecked)
            }
        }

    }

    /**
     * Settings fo the dropDown Menus : type of real estate and agent's name
     */
    private fun dropDownMenusSettings(binding: FragmentCreateBinding, context: Context) {
        //for type of real estate menu
        typeAdapterBuilder(binding.tvType)
        //listener
        binding.tvType.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateType(BuildingType.entries[position])
        }

        //for agent's name menu
        //TODO : modifier la fonction pour recup les agents sans observer pcq pas besoin
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

    private fun typeAdapterBuilder(typeTv : AutoCompleteTextView){
        typeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_create,
            BuildingType.entries.map { ContextCompat.getString(context, it.displayName) }
        )
        typeTv.setAdapter(typeAdapter)
    }

}