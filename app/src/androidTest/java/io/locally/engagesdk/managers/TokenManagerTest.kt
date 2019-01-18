package io.locally.engagesdk.managers

import android.content.Context
import android.support.test.InstrumentationRegistry
import io.locally.engagesdk.common.Utils
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class TokenManagerTest {

    private lateinit var context: Context

    @Before
    fun setup(){
        context = InstrumentationRegistry.getTargetContext().applicationContext
        Utils.init(context)

    }

    @Test
    fun testTokenValid() {
        val calendar = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.DATE, 7)
        }
        Utils.refreshDate = calendar.time

        assertEquals(true, TokenManager.isTokenValid)
    }

    @Test
    fun testTokenInvalid() {
        val calendar = Calendar.getInstance().apply {
            time = Date()
            set(Calendar.DATE, 10)
        }
        Utils.refreshDate = calendar.time

        assertEquals(false, TokenManager.isTokenValid)
    }
}