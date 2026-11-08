package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.utils.DateUtils
import com.example.core.utils.FormatUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Daily Life Tracker", appName)
  }

  @Test
  fun `format rupiah displays correct currency string`() {
    val formatted = FormatUtils.formatRupiah(50000)
    assertTrue(formatted.contains("50.000") || formatted.contains("Rp"))
  }

  @Test
  fun `date utils generates valid today id`() {
    val today = DateUtils.todayId()
    assertNotNull(today)
    assertEquals(10, today.length) // yyyy-MM-dd
  }
}

