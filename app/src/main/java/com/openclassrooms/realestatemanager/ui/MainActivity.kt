package com.openclassrooms.realestatemanager.ui


import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.openclassrooms.realestatemanager.AppApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.ui.list_map_details.LeftPanelFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //to deal with edge to edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, windowInsets ->
            //get the insets for system bars (status and navigation)
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            //Apply insets as padding to the view
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)

            //Return insets so child views can use it too
            WindowInsetsCompat.CONSUMED
        }

        //if its the first time MainActivity is launched, display Sliding Pane Layout
        if (savedInstanceState == null) {
            replaceFragment()
        }

        //request notification permission
        requestPermissionLauncher()

    }

    /**
     * Method to make the LeftPanelFragment the first fragment displayed
     * when the application is running
     */
    private fun replaceFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentSlidingPanelayoutContainer.id, LeftPanelFragment.newInstance())
        }
    }


    /**
     * Launcher to request permission for notifications
     */
    private fun requestPermissionLauncher() {

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    AppApplication.appContext,
                    "Notification permission denied, please enable permission.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    }


}
