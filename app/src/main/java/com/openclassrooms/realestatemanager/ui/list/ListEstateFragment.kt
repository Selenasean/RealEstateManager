package com.openclassrooms.realestatemanager.ui.list

import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.FragmentEstateListBinding
import com.openclassrooms.realestatemanager.databinding.RealestateItemBinding
import com.openclassrooms.realestatemanager.domain.RealEstate
import com.openclassrooms.realestatemanager.ui.MainAdapter
import com.openclassrooms.realestatemanager.ui.detail.DetailFragment

class ListEstateFragment : Fragment() {

    companion object{
        fun newInstance() = ListEstateFragment()
    }

    private val realEstateList: List<RealEstate> = listOf(
        RealEstate("0", "Pistache", 1000),
        RealEstate("1", "Fraise", 1100),
        RealEstate("2", "Ananas", 2000),
        RealEstate("3", "Fruit de la Passion", 12000),
        RealEstate("4", "Pomme", 800),
        RealEstate("5", "Durian", 9000)
    )

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
        setRecyclerView(binding)
    }

    private fun setRecyclerView(binding: FragmentEstateListBinding) {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        val adapter = MainAdapter(clickedListener = { id: String ->
            binding.slidingPaneLayout.openPane()
            childFragmentManager.commit {
                replace(binding.fragmentDetailContainer.id, DetailFragment.newInstance())
            }
        })
        adapter.submitList(realEstateList)
        recyclerView.adapter = adapter
    }
}