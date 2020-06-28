/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.runner.clock

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Timer
import java.util.TimerTask

@ExtendWith(MockKExtension::class)
internal class ClockTest {

    @RelaxedMockK
    private lateinit var timer: Timer

    @InjectMockKs
    private lateinit var clock: Clock

    @Test
    fun `starting a clock should schedule a new timer task`() {
        val onTick = mockk<(Long) -> Unit>()

        clock.start(onTick)

        verify { timer.schedule(any(), 0L, 1000L) }
    }

    @Test
    fun `should invoke the onTick function when the timer task is invoked`() {
        val onTick = mockk<(Long) -> Unit>(relaxed = true   )
        val timerTaskSlot = slot<TimerTask>()
        every { timer.schedule(capture(timerTaskSlot), any<Long>(), any()) } just Runs

        clock.start(onTick)

        timerTaskSlot.captured.run()

        verify { onTick(withArg {
            assertThat(it).isLessThan(TOLERANCE_MS)
        }) }
    }

    companion object {
        private const val TOLERANCE_MS = 100L
    }
}