package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify project branding`() {
    val appName = "Project 90"
    assertEquals("Project 90", appName)
  }
}
