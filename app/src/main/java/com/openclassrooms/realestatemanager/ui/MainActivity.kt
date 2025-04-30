package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.list_map_details.LeftPanelFragment
import com.openclassrooms.realestatemanager.ui.list_map_details.ListMapDetailViewModel
import kotlin.getValue

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
