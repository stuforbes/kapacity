package com.stuforbes.kapacity.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test

internal class TimeKtTest {

    @Test
    fun `should return the current time`() {
        val now = now()
        val time = System.currentTimeMillis()
        assertThat(now).isCloseTo(time, offset(1000L))
    }
}