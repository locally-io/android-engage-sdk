package io.locally.engagesdk

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.locally.engagesdk.common.Utils
import io.locally.engagesdk.datamodels.impression.Demographics
import io.locally.engagesdk.datamodels.impression.Demographics.Gender.MALE
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemographicsTest {

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        Utils.init(context)
    }

    @Test
    fun demographicsSuccess(){
        Utils.demographics = Demographics(age = 27, gender = MALE)

        assertNotNull(Utils.demographics)
        assertEquals(27, Utils.demographics?.age)
        assertEquals(MALE, Utils.demographics?.gender)
    }

    @Test
    fun demographicsFail(){
        Utils.demographics = null

        assertNull(Utils.demographics)
    }
}