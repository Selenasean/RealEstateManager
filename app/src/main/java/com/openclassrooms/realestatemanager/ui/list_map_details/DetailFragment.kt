package com.openclassrooms.realestatemanager.ui.list_map_details

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import coil.decode.VideoFrameDecoder
import coil.load
import coil.request.videoFrameMillis
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.Position
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.create_edit.CreateEditFragment
import com.openclassrooms.realestatemanager.utils.CurrencyCode
import com.openclassrooms.realestatemanager.utils.FragmentViewBindingDelegate
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
        setRecyclerView()
        binding.noItemLytContainer.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailState.collect { realEstateDetailed ->
                    render(
                        realEstateDetailed
                    )
                }
            }
        }
        val pathVideo = ("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.sample_video).toUri()
        if (viewModel.isVideoPlaying) {
            playVideo(pathVideo)
        }

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
            val pathVideo =
                ("android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.sample_video).toUri()
            //display video thumbnail
            binding.videoThumbnail.load(pathVideo) {
                crossfade(true)
                videoFrameMillis(1000)
            }

            //listener for play button
            binding.playBtn.setOnClickListener { playVideo(pathVideo) }

        }


    }

    private fun playVideo(pathVideo: Uri) {
        //hide thumbnail
        binding.videoThumbnail.visibility = View.GONE
        binding.playBtn.visibility = View.GONE

        //set the video
        binding.videoView.setVideoURI(pathVideo)
        binding.videoView.requestFocus()

        //settings for dealing with video running
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            //get the last position of playback
            mediaController?.show()
            val lastPosition = viewModel.playbackPosition.value
            if(lastPosition > 0){
                mediaPlayer.seekTo(lastPosition)
            }
            binding.videoView.start()
            viewModel.isVideoPlaying = true
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
        viewModel.isVideoPlaying = false
        binding.videoThumbnail.visibility = View.VISIBLE
        binding.playBtn.visibility = View.VISIBLE
        mediaController?.hide()
    }

    /**
     * To setup the mediaController
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

    override fun onPause() {
        super.onPause()
        if(viewModel.isVideoPlaying){
            viewModel.updatePlaybackPosition(binding.videoView.currentPosition)
        }
        viewModel.isVideoPlaying = false
        binding.videoView.stopPlayback()
    }

}




