package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.domain.RealEstate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val realEstateList: List<RealEstate> = listOf(
        RealEstate("0", "Pistache", 1000),
        RealEstate("1", "Fraise", 1100),
        RealEstate("2", "Ananas", 2000),
        RealEstate("3", "Fruit de la Passion", 12000),
        RealEstate("4", "Pomme", 800),
        RealEstate("5", "Durian", 9000)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()
    }

    private fun setRecyclerView() {
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val adapter = MainAdapter(clickedListener = { id: String ->
            binding.slidingPaneLayout.openPane()
            supportFragmentManager.commit {
                replace(binding.fragmentDetailContainer.id, DetailFragment.newInstance())
            }
        })
        adapter.submitList(realEstateList)
        recyclerView.adapter = adapter
    }



}