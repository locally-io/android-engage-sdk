package io.locally.engagesdk.managers

import android.content.Context
import android.support.test.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocationManagerTestFailure {

    private lateinit var context: Context
    private lateinit var locationManager: LocationManager

    @Before
    fun setup(){
        context = InstrumentationRegistry.getTargetContext().applicationContext
        locationManager = LocationManager(context)
    }

    @Test
    fun testManagerFailure() {
        locationManager.currentLocation { location ->
            assertEquals(null, location)
        }

        Thread.sleep(5000)
    }
}