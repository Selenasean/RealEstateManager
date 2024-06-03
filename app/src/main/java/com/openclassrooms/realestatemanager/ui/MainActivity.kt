package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.domain.RealEstate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var realEstateList: List<RealEstate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize real estates list
        fakeRealEstatesBuild()

        setRecyclerView()
    }

    private fun setRecyclerView() {
         val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val adapter = MainAdapter(realEstateList)
        recyclerView.adapter = adapter
    }

    private fun fakeRealEstatesBuild(){
        realEstateList = listOf(
            RealEstate(0, "Pistache", 1000),
            RealEstate(0, "Fraise", 1100),
            RealEstate(0, "Ananas", 2000),
            RealEstate(0, "Fruit de la Passion", 12000),
            RealEstate(0, "Pomme", 800),
            RealEstate(0, "Durian", 9000)
        )
    }


}