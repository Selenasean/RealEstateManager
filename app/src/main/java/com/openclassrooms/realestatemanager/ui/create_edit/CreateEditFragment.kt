package com.openclassrooms.realestatemanager.ui.create_edit

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Amenity
import com.openclassrooms.realestatemanager.data.model.BuildingType
import com.openclassrooms.realestatemanager.data.model.Status
import com.openclassrooms.realestatemanager.databinding.FragmentCreateBinding
import com.openclassrooms.realestatemanager.domain.notifications.NotificationHelper
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.list_map_details.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.PhotoSelectedViewState
import com.openclassrooms.realestatemanager.utils.events.CreationEvents
import com.openclassrooms.realestatemanager.utils.events.observeAsEvents
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateEditFragment : BottomSheetDialogFragment(R.layout.fragment_create) {

    companion object {
        const val TAG = "CREATE_BOTTOM_SHEET"
        const val CLASS_NAME = "CREATE_FRAGMENT"
        const val REAL_ESTATE_ID = "REAL_ESTATE_ID"

        fun newInstance(id: String?) = CreateEditFragment().apply {
            //put id in argument within bundle
            arguments = bundleOf(REAL_ESTATE_ID to id)
        }

    }

    private val viewModel by viewModels<CreateEditViewModel> { ViewModelFactory.Companion.getInstance() }
    private lateinit var adapter: PhotosAdapter
    private var currentPhotoUri: Uri? = null
    private lateinit var typeAdapter: ArrayAdapter<String>
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateBinding.bind(view)
        val context = binding.root.context

        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED

        typeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu,
            BuildingType.entries.map { ContextCompat.getString(context, it.displayName) }
        )


        //settings for dropDown menus, viewModel & recyclerView
        dropDownMenusSettings(binding, context)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    render(it, binding, context)
                }
            }
        }
        setRecyclerView(binding)

        //create a realEstate
        binding.saveBtn.setOnClickListener {
            viewModel.saveRealEstate()
            Log.i("CreateFragment", "onViewCreated:  create RealEstate")
            binding.textInputLytAddress.helperText = null
        }

        //observe events and do something according to it
        creationEventsResult(binding)

        //inputs listeners
        inputsListeners(binding)

        //photo picker media & listener
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                if (uris.isNotEmpty()) {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    uris.forEach { uri ->
                        context.contentResolver.takePersistableUriPermission(uri, flag)
                    }
                    viewModel.addPhotoPicker(uris)

                }
            }

        binding.selectPictureBtn.setOnClickListener {
            openPhotoPicker(pickMedia)
        }

        //take picture from device
        val takePictureCallback =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { successful ->
                if (successful) {
                    viewModel.addPhotoTaken(currentPhotoUri.toString())
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

        //videoPicker

        //Select date of sale
        binding.datePickerBtn.setOnClickListener {
            showDatePicker(binding)
        }

    }

    private fun inputsListeners(binding: FragmentCreateBinding) {
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
        binding.chipForSale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.updateStatus(Status.FOR_SALE)
                binding.datePickerLyt.visibility = View.GONE
                viewModel.updateDateOfSale(null)
            }
        }
        binding.chipSold.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.updateStatus(Status.SOLD)
                binding.datePickerLyt.visibility = View.VISIBLE
            }
            Log.i("chipSold", "chip sold : $isChecked")
        }
    }

    /**
     * Define the events observe to do something according to it
     * @param binding, which is the binding of this fragment
     */
    private fun creationEventsResult(binding: FragmentCreateBinding) {
        // instantiate the class with notifications logic
        val notificationHelper = NotificationHelper(requireContext())

        observeAsEvents(viewModel.isCreatedFlow) { event ->
            when (event) {

                CreationEvents.isCreated -> {
                    //check notification permission and send notification for creation
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when {
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                notificationHelper.sendCreationNotification()
                            }

                            else -> {
                                //ask user to enable the permission
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.enable_notification_permission),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        //no runtime permission needed for older versions
                        notificationHelper.sendCreationNotification()
                    }
                    dismiss()

                }

                CreationEvents.isInternetAvailable -> Toast.makeText(
                    requireContext(), R.string.no_internet,
                    Toast.LENGTH_SHORT
                ).show()

                CreationEvents.isUpdated -> {
                    //check notification permission and send notification for update
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when {
                            ContextCompat.checkSelfPermission(
                                requireContext(),
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                notificationHelper.sendUpdateNotification()
                            }

                            else -> {
                                //ask user to enable the permission
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.enable_notification_permission),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        //no runtime permission needed for old versions
                        notificationHelper.sendUpdateNotification()
                    }
                    dismiss()
                }

                CreationEvents.failure -> {
                    binding.textInputLytAddress.helperText =
                        ContextCompat.getString(requireContext(), R.string.address_invalid)

                    Toast.makeText(requireContext(), R.string.failure, Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }

    private fun showDatePicker(binding: FragmentCreateBinding) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { datePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                //format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                //display the selected date
                binding.datePickedTv.text = "$formattedDate"
                //save the selected date in the state
                viewModel.updateDateOfSale(selectedDate.toInstant())
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    /**
     * Display recyclerView of photos or not depending on the size of the photo's list
     */
    private fun render(
        it: RealEstateToSaveState,
        binding: FragmentCreateBinding,
        context: Context
    ) {
        if (!viewModel.isCreation) {
            binding.titleTv.text = getString(R.string.update_title)
            binding.lytStatus.visibility = View.VISIBLE
            binding.datePickerLyt.visibility = View.VISIBLE
        } else {
            binding.lytStatus.visibility = View.GONE
            binding.datePickerLyt.visibility = View.GONE
        }

        binding.saveBtn.isEnabled = it.isInputsCompleted()

        //render type
        if (it.type != null && binding.tvType.text.toString() != it.type.name) {
            binding.tvType.setText(ContextCompat.getString(context, it.type.displayName), false)
        }
        //render photo
        if (it.photos.isEmpty()) {
            binding.rvPhotosSelected.visibility = View.GONE
        } else {
            binding.rvPhotosSelected.visibility = View.VISIBLE
            adapter.submitList(it.photos)
        }
        //render address, city
        if (binding.tvAddress.text.toString() != it.address) {
            binding.tvAddress.setText(it.address)
        }
        if (binding.tvCity.text.toString() != it.city) {
            binding.tvCity.setText(it.city)
        }
        //render price
        if (binding.tvPrice.text.toString() != it.price) {
            binding.tvPrice.setText(it.price)
        }
        //render surface
        if (binding.tvSurface.text.toString() != it.surface) {
            binding.tvSurface.setText(it.surface)
        }
        //render rooms
        if (binding.tvRooms.text.toString() != it.rooms) {
            binding.tvRooms.setText(it.rooms)
        }
        //render bedrooms
        if (binding.tvBedrooms.text.toString() != it.bedrooms) {
            binding.tvBedrooms.setText(it.bedrooms)
        }
        //render bathrooms
        if (binding.tvBathrooms.text.toString() != it.bathrooms) {
            binding.tvBathrooms.setText(it.bathrooms)
        }
        //render description
        if (binding.tvDescription.text.toString() != it.description) {
            binding.tvDescription.setText(it.description)
        }
        //render amenities
        renderAmenitiesChips(binding, context, it.amenities)

        //render agent's name
        if (it.agent != null && binding.tvAgent.text.toString() != it.agent.name) {
            binding.tvAgent.setText(it.agent.name, false)
        }
        //render status
        if (it.status == Status.FOR_SALE) binding.datePickerLyt.visibility =
            View.GONE else View.VISIBLE
        renderStatusChips(binding, it.status)

        //render dateOfSale
        if (it.dateOfSale == null) {
            binding.datePickedTv.text = ""
        } else {
            val formatter = dateFormat.format(it.dateOfSale.toEpochMilli())
            binding.datePickedTv.text = formatter
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
    private fun renderAmenitiesChips(
        binding: FragmentCreateBinding,
        context: Context,
        amenitiesSelected: List<Amenity>
    ) {
        val chipsGroup = binding.chipGroup
        chipsGroup.removeAllViews()

        Amenity.entries.forEach { amenity ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.chip_layout, chipsGroup, false) as Chip
            chip.text = ContextCompat.getString(context, amenity.displayName)

            chipsGroup.addView(chip)

            chip.isChecked = amenitiesSelected.contains(amenity)

            //listener
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateAmenities(amenity, isChecked)
            }
        }

    }


    private fun renderStatusChips(binding: FragmentCreateBinding, statusSelected: Status) {
        binding.chipForSale.isChecked = statusSelected == Status.FOR_SALE
        binding.chipSold.isChecked = statusSelected == Status.SOLD
    }

    /**
     * Settings fo the dropDown Menus : type of real estate and agent's name
     */
    private fun dropDownMenusSettings(binding: FragmentCreateBinding, context: Context) {
        //for type of real estate menu
        typeAdapterBuilder(binding.tvType, context)
        //listener
        binding.tvType.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateType(BuildingType.entries[position])
        }

        //for agent's name menu
        agentsAdapterSettings(binding.tvAgent)

    }

    private fun agentsAdapterSettings(tvAgent: AutoCompleteTextView) {
        viewLifecycleOwner.lifecycleScope.launch {
            val agents = viewModel.fetchAgents()
            val agentAdapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu,
                agents.map { agent -> agent.name }
            )
            tvAgent.setAdapter(agentAdapter)
            //listener
            tvAgent.setOnItemClickListener { _, _, position, _ ->
                viewModel.updateAgentName(agents[position])
            }
        }
    }

    private fun typeAdapterBuilder(typeTv: AutoCompleteTextView, context: Context) {
        typeAdapter = ArrayAdapter(
            context,
            R.layout.dropdown_menu,
            BuildingType.entries.map { it.name }
        )
        typeTv.setAdapter(typeAdapter)
    }
}