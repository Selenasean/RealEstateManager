package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.Position
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.create_edit.CreateEditFragment
import com.openclassrooms.realestatemanager.utils.CurrencyCode
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.viewBinding
import kotlinx.coroutines.launch

class DetailFragment : Fragment(R.layout.fragment_detail), OnMapReadyCallback {

    companion object {
        fun newInstance() = DetailFragment()
        const val CLASS_NAME = "DETAIL_FRAGMENT"
    }

    //only valid between onCreateView an onDestroyView
    private val binding by viewBinding(FragmentDetailBinding::bind)

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }
    private lateinit var photosAdapter: PhotosAdapter
    private var mediaController: MediaController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //settings for recyclerView
        setRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailState.collect { realEstateDetailed ->
                    render(
                        realEstateDetailed
                    )
                }
            }
        }

        //update btn listener
        binding.fabUpdate.setOnClickListener {
            //pass id to CreateEditFragment
            val id = viewModel.detailState.value?.id
            if (id != null) {
                val createBottomSheet = CreateEditFragment.newInstance(
                    id = id
                )
                val fragmentManager = (activity as FragmentActivity).supportFragmentManager
                fragmentManager.let { createBottomSheet.show(it, CreateEditFragment.TAG) }
            }


        }

        //map view displayed
        val mapView = binding.mapSnapshot
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        //setup for mediaController
        setupMediaController(binding.videoView)

        //listener for  video play button
        binding.playBtn.setOnClickListener {
            val currentVideoUri = viewModel.detailState.value?.video
            if (currentVideoUri != null) {
                playVideo(currentVideoUri)
            }
        }
    }

    /**
     * To render elements on UI
     */
    private fun render(
        realEstate: RealEstateDetailViewState?
    ) {
        if (realEstate == null) {
            binding.noItemLytContainer.visibility = View.VISIBLE
            binding.constraintlayoutContainer.visibility = View.GONE
            binding.fabUpdate.visibility = View.GONE
        } else {
            binding.fabUpdate.visibility = View.VISIBLE
            val context = binding.root.context
            binding.constraintlayoutContainer.visibility = View.VISIBLE
            binding.noItemLytContainer.visibility = View.GONE

            //linked to adapter
            photosAdapter.submitList(realEstate.photos)

            binding.descriptionContent.text = realEstate.description
            binding.lytAttributes.statusValueTv.text =
                ContextCompat.getString(context, realEstate.status.state)
            if (realEstate.status.state == R.string.for_sale) {
                binding.lytAttributes.statusDisplayCard
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.md_theme_tertiaryFixed_mediumContrast
                        )
                    )
                binding.lytAttributes.statusValueTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_onTertiary_mediumContrast
                    )
                )
                binding.lytAttributes.dateOfSaleTv.visibility = View.GONE
            } else {
                binding.lytAttributes.statusDisplayCard
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.md_theme_error_mediumContrast
                        )
                    )
                binding.lytAttributes.statusValueTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_onError_mediumContrast
                    )
                )
                binding.lytAttributes.dateOfSaleTv.visibility = View.VISIBLE
                binding.lytAttributes.dateOfSaleTv.text =
                    getString(R.string.since).plus(" " + realEstate.dateOfSale)
            }
            binding.lytAttributes.priceValueTv.text =
                Utils.priceFormatter(price = realEstate.priceTag, CurrencyCode.EURO)
            binding.lytLocationAmenities.cityTv.text = realEstate.city
            binding.lytAttributes.surfaceValueTv.text = realEstate.surface.toString()
            binding.lytAttributes.roomsValueTv.text = realEstate.rooms.toString()
            binding.lytAttributes.bedroomsValueTv.text = realEstate.bedrooms.toString()
            binding.lytAttributes.bathroomsValueTv.text = realEstate.bathrooms.toString()
            binding.lytLocationAmenities.locationValueTv.text = realEstate.address
            val amenitiesString =
                realEstate.amenities.map { ContextCompat.getString(context, it.displayName) }
            binding.lytLocationAmenities.amenitiesValueTv.text = amenitiesString.joinToString(", ")
            viewModel.realEstatePosition(
                Position(
                    latitude = realEstate.latitude!!,
                    longitude = realEstate.longitude!!
                )
            )

            //for video
            if (viewModel.oldId != realEstate.id) {
                stopVideo()
                //hide videoView frame with a drawable
                binding.videoView.setBackgroundResource(R.color.design_default_color_on_secondary)
            }
            if (realEstate.video != null) {
                binding.videoContainer.visibility = View.VISIBLE
                binding.textviewDetailVideo.visibility = View.VISIBLE
            } else {
                binding.videoContainer.visibility = View.GONE
                binding.textviewDetailVideo.visibility = View.GONE
            }

        }
        viewModel.oldId = realEstate?.id
    }

    /**
     * To play video in VideoView
     * @param pathVideo video's path
     */
    private fun playVideo(pathVideo: String) {
        //display videoView frame
        binding.videoView.setBackgroundResource(0)
        binding.playBtn.visibility = View.GONE

        //set the video
        binding.videoView.setVideoURI(pathVideo.toUri())

        //settings for dealing with video running
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            //get the last position of playback
            mediaController?.show()
            val lastPosition = viewModel.playbackPosition.value
            if (lastPosition > 0) {
                mediaPlayer.seekTo(lastPosition)
            }
            binding.videoView.start()
            viewModel.onVideoStarted(pathVideo)
        }

        binding.videoView.setOnCompletionListener {
            stopVideo()
        }
    }

    /**
     * To stop video playing
     */
    fun stopVideo() {
        binding.videoView.stopPlayback()
        viewModel.onVideoStopped()
        binding.playBtn.visibility = View.VISIBLE
        mediaController?.hide()
    }

    /**
     * To setup the mediaController
     * @param videoView
     */
    private fun setupMediaController(videoView: VideoView) {
        mediaController = MediaController(context)
        mediaController?.setAnchorView(videoView.parent as View)
        videoView.setMediaController(mediaController)
    }

    /**
     * RV settings
     */
    private fun setRecyclerView() {
        val recyclerView = binding.imagesRv
        photosAdapter =
            PhotosAdapter(CLASS_NAME, labelClickedListener = {}, onDeleteClickedListener = {})
        recyclerView.adapter = photosAdapter
    }

    /**
     * onMapReady callback
     * @param map
     */
    override fun onMapReady(map: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.positionStateFlow.collect { position ->
                    if (position != null) {
                        renderMap(map, position)
                    }
                }
            }
        }
    }

    /**
     * Displaying markers on map according real estate's position
     * @param map
     * @param position
     */
    private fun renderMap(map: GoogleMap, position: Position) {
        map.clear()
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    position.latitude,
                    position.longitude
                ), 10f
            )
        )
        map.addMarker(
            MarkerOptions().position(
                LatLng(
                    position.latitude,
                    position.longitude
                )
            )
        )
    }

    /**
     * To stop or pause the video when UI is paused
     * Keep the last position to play the video where it was stopped
     */
    override fun onPause() {
        super.onPause()
        if (viewModel.playingVideo != null) {
            viewModel.updatePlaybackPosition(binding.videoView.currentPosition)
        }
    }

    /**
     * To clean videoView listeners, mediaController and playBack
     * when UI is destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        binding.videoView.setOnPreparedListener(null)
        binding.videoView.setOnCompletionListener(null)

        mediaController?.hide() // Hide it first
        mediaController = null

        binding.videoView.stopPlayback()
    }

}




