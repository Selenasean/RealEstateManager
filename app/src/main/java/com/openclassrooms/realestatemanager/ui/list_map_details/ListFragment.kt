package com.openclassrooms.realestatemanager.ui.list_map_details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.ui.ViewModelFactory

// the fragment initialization parameters
private const val ARG_PARAM1 = "param1"

class ListFragment : Fragment(){
    
    private val viewModel by activityViewModels<ListMapDetailViewModel> { ViewModelFactory.getInstance() }
    private lateinit var adapter: ListAdapter

    companion object{
        fun newInstance() = ListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentListBinding.inflate(inflater, container, false).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentListBinding.bind(view)

        viewModel.listState.observe(viewLifecycleOwner, Observer { listItemState ->
            render(listItemState)
        })
        
        //settings for RV
        setRecyclerView(binding)
    }

    private fun render(listItemState: List<ItemState>)  {
        adapter.submitList(listItemState)
    }

    private fun setRecyclerView(binding: FragmentListBinding) {
        val recyclerView: RecyclerView = binding.recyclerView

        recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = ListAdapter(clickedListener = {id: String ->
            //open the Pane of SlidingPanel Layout
            viewModel.onRealEstateClick(id)
        })

        recyclerView.adapter = adapter
    }
}
