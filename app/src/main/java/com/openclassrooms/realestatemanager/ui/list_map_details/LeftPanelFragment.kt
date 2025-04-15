package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentLeftPanelBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.create.CreateFragment
import com.openclassrooms.realestatemanager.utils.events.EventDetailPane
import com.openclassrooms.realestatemanager.utils.observeAsEvents

class LeftPanelFragment : Fragment() {

    companion object {
        fun newInstance() = LeftPanelFragment()
    }

    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentLeftPanelBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLeftPanelBinding.bind(view)
        val slidingPaneLayout = binding.slidingPaneLayout

        // We need to wait for layout as the ListOnBackPressCallback access the view
        // properties that are only correct after first layout. Not doing that causes the back press
        // handler being disabled after process death when the details panel is open
        binding.slidingPaneLayout.doOnLayout {
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                //callback to return on the leftPane from SlidingPaneLayout when clicked on return button of the device
                ListOnBackPressCallback(slidingPaneLayout, viewModel)
            )

        }

        //Event to open Detail Pane from the SlidingPaneLayout
        observeAsEvents(viewModel.eventsFlow) { event ->
            when (event) {
                is EventDetailPane.OpenDetails -> slidingPaneLayout.openPane()
            }
        }

        //by default it's the list fragment displayed
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(binding.childFragmentContainer.id, ListFragment.newInstance())
            }

        }

        //to implement the menu on topAppBar
        val mapItemId = binding.topAppBar.menu.findItem(R.id.app_bar_map)
        val listIdItem = binding.topAppBar.menu.findItem(R.id.app_bar_list)
        mapItemId.isVisible = true
        listIdItem.isVisible = false
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.app_bar_create -> {
                    val createBottomSheet = CreateFragment()
                    val fragmentManager = (activity as FragmentActivity).supportFragmentManager
                    fragmentManager.let { createBottomSheet.show(it, CreateFragment.TAG) }
                    true
                }

                R.id.app_bar_filter -> {
                    Log.i("listEstateFragment", "filter")
                    true
                }

                R.id.app_bar_list -> {
                    childFragmentManager.commit {
                        replace(R.id.childFragmentContainer, ListFragment.newInstance())
                    }
                    listIdItem.isVisible = false
                    mapItemId.isVisible = true
                    true
                }

                R.id.app_bar_map -> {
                    childFragmentManager.commit {
                        replace(R.id.childFragmentContainer, MapFragment.newInstance())
                    }
                    mapItemId.isVisible = false
                    listIdItem.isVisible = true
                    true
                }

                else -> false
            }
        }

    }

    /**
     * Callback to backPress from the detail view to the list
     * Only when the slidingPaneLayout is slideable and open
     */
    class ListOnBackPressCallback(
        private val slidingPaneLayout: SlidingPaneLayout,
        private val viewModel: ListMapDetailViewModel
    ) : OnBackPressedCallback(slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen),
        SlidingPaneLayout.PanelSlideListener {
        override fun handleOnBackPressed() {
            slidingPaneLayout.closePane()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) {
        }

        override fun onPanelOpened(panel: View) {
            //allows to backPress to the list view
            isEnabled = true

        }

        override fun onPanelClosed(panel: View) {
            isEnabled = false
            viewModel.onDetailClosed()
        }

        init {
            slidingPaneLayout.addPanelSlideListener(this)
        }

    }

}