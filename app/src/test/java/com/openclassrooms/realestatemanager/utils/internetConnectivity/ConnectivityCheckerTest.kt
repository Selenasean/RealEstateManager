package com.openclassrooms.realestatemanager.utils.internetConnectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities
import org.robolectric.shadows.ShadowNetworkInfo

@RunWith(RobolectricTestRunner::class)
class ConnectivityCheckerTest {

    private lateinit var connectivityChecker: ConnectivityChecker
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        //get the context to get connectivityManager from Robolectric
        val context = ApplicationProvider.getApplicationContext<Context>()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityChecker = ConnectivityChecker(connectivityManager)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.M])
    //for more recent app
    fun is_internet_available_with_api_level_23_or_more() {
        //GIVEN
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val capabilities : NetworkCapabilities = ShadowNetworkCapabilities.newInstance()

        //decide of the state and behavior of framework objects
        Shadows.shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        Shadows.shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        shadowConnectivityManager.setNetworkCapabilities(activeNetwork,capabilities )
        //WHEN
        val isAvailable = connectivityChecker.isInternetAvailable()
        //THEN
        assertThat(isAvailable).isTrue()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP_MR1])
    //for oldest app
    fun is_internet_available_with_api_22_or_less() {
        //GIVEN
        val activeNetworkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED, 0,0,true,
            NetworkInfo.State.CONNECTED)
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(activeNetworkInfo)
        //WHEN
        val isAvailable = connectivityChecker.isInternetAvailable()
        //THEN
        assertThat(isAvailable).isTrue()
    }
}