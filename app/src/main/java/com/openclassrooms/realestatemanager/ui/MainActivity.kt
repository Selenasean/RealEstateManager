package com.openclassrooms.realestatemanager.ui


import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.commit
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.list_map_details.LeftPanelFragment
import com.openclassrooms.realestatemanager.ui.list_map_details.ListMapDetailViewModel
import com.openclassrooms.realestatemanager.utils.Utils

import java.time.Instant


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null){
            replaceFragment()
        }
        
    }

    private fun replaceFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentSlidingPanelayoutContainer.id, LeftPanelFragment.newInstance())
        }
    }



}
