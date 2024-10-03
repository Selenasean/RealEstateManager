package com.openclassrooms.realestatemanager.ui.list_and_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentEstateListBinding
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.ui.ViewModelFactory

class ListEstateFragment : Fragment() {

    companion object {
        fun newInstance() = ListEstateFragment()
    }


    private val viewModel by activityViewModels<ListDetailViewModel> { ViewModelFactory.getInstance() }
    private lateinit var adapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEstateListBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEstateListBinding.bind(view)
        val slidingPaneLayout = binding.slidingPaneLayout
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            ListOnBackPressCallback(slidingPaneLayout, viewModel)
        )
        //to implement the menu on topAppBar
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
                else -> false
            }
        }


        viewModel.listState.observe(viewLifecycleOwner, Observer { listItemState ->
            render(listItemState, binding)


        })
        setRecyclerView(binding, slidingPaneLayout)
    }


    private fun render(listItemState: List<ItemState>, binding:FragmentEstateListBinding) {
        adapter.submitList(listItemState)

    }

    private fun setRecyclerView(
        binding: FragmentEstateListBinding,
        slidingPaneLayout: SlidingPaneLayout
    ) {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = ListAdapter(clickedListener = { id: Long ->
            slidingPaneLayout.openPane()
            childFragmentManager.commit {
                replace(binding.fragmentDetailContainer.id, DetailFragment.newInstance())
            }
            viewModel.onRealEstateClick(id)
        })
        recyclerView.adapter = adapter
    }
}

/**
 * Callback to backPress from the detail view to the list
 * Only when the slidingPaneLayout is slideable and open
 */
class ListOnBackPressCallback(
    private val slidingPaneLayout: SlidingPaneLayout,
    private val viewModel: ListDetailViewModel
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