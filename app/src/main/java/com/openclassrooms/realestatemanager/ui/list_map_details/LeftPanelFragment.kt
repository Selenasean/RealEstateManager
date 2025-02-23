package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.openclassrooms.realestatemanager.R

import com.openclassrooms.realestatemanager.databinding.FragmentLeftPanelBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory
import com.openclassrooms.realestatemanager.ui.create.CreateFragment
import com.openclassrooms.realestatemanager.utils.observeAsEvents

class ListEstateFragment : Fragment() {

    companion object {
        fun newInstance() = ListEstateFragment()
    }


    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }
//    private lateinit var adapter: ListAdapter

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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            ListOnBackPressCallback(slidingPaneLayout, viewModel)
        )

        observeAsEvents(viewModel.eventsFlow){ event ->
            when(event){
                is Event.OpenDetails -> slidingPaneLayout.openPane()
            }
        }

        if(savedInstanceState == null){
            childFragmentManager.commit {
                replace(binding.childFragmentContainer.id, ListFragment.newInstance())
            }
        }else{
            if(viewModel.isDetailOpen()){
                slidingPaneLayout.openPane()
            }

        }
//        viewModel.isPaneOpen.observe(viewLifecycleOwner, Observer { isOpen ->
//            if(isOpen){
//                slidingPaneLayout.openPane()
//                childFragmentManager.commit {
//                    replace(binding.fragmentDetailContainer.id, DetailFragment.newInstance())
//                }
//            }
//        })

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
                R.id.app_bar_list -> {
                    childFragmentManager.commit {
                        add(R.id.childFragmentContainer, ListFragment.newInstance())
                        Log.i("chilFragmentContainer", "ca passe")
                    }
                    true
                }
                R.id.app_bar_map -> {
                    true
                }
                else -> false
            }
        }


//        viewModel.listState.observe(viewLifecycleOwner, Observer { listItemState ->
//            render(listItemState, binding)
//
//
//        })
//        setRecyclerView(binding, slidingPaneLayout)
    }


//    private fun render(listItemState: List<ItemState>, binding:FragmentLeftPanelBinding) {
//        adapter.submitList(listItemState)
//
//    }

//    private fun setRecyclerView(
//        binding: FragmentLeftPanelBinding,
//        slidingPaneLayout: SlidingPaneLayout
//    ) {
//        val recyclerView: RecyclerView = binding.recyclerView
//        recyclerView.setLayoutManager(LinearLayoutManager(context))
//        adapter = ListAdapter(clickedListener = { id: Long ->
//            slidingPaneLayout.openPane()
//
//
//            childFragmentManager.commit {
//                replace(binding.fragmentDetailContainer.id, DetailFragment.newInstance())
//            }
//            viewModel.onRealEstateClick(id)
//        })
//        recyclerView.adapter = adapter
//    }
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