/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.runner

import com.stuforbes.kapacity.model.TimeSeriesData
import com.stuforbes.kapacity.runner.clock.Clock
import com.stuforbes.kapacity.runner.data.DataPoster
import com.stuforbes.kapacity.test.aDataPoint
import com.stuforbes.kapacity.test.arbitraryValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PerformanceTestRunnerTest {

    @MockK
    private lateinit var clockFactory: () -> Clock

    @RelaxedMockK
    private lateinit var clock: Clock

    @RelaxedMockK
    private lateinit var dataPoster: DataPoster<String>

    @MockK
    private lateinit var timeSeriesDataProgression: TimeSeriesDataProgression<String>

    @RelaxedMockK
    private lateinit var onComplete: () -> Unit

    @InjectMockKs
    private lateinit var performanceTestRunner: PerformanceTestRunner<String>

    private lateinit var data: List<TimeSeriesData<String>>

    @BeforeEach
    fun before() {
        data = listOf(aDataPoint(1), aDataPoint(2), aDataPoint(3))

        mockkStatic("com.stuforbes.kapacity.runner.TimeSeriesDataProgressionKt")
        every { data.asProgression() } returns timeSeriesDataProgression

        every { clockFactory() } returns clock
    }

    @Test
    fun `should create a new clock instance when starting a run`() {
        performanceTestRunner.doRun(arbitraryValue(), data, onComplete)

        verify {
            clockFactory()
            clock.start(any())
        }
    }

    @Nested
    inner class ClockTick {

        private lateinit var onTick: (Long) -> Unit

        private var maxDuration: Long = 0L

        @BeforeEach
        fun before() {
            maxDuration = arbitraryValue()

            val slot = slot<(Long) -> Unit>()
            every { clock.start(capture(slot)) } just Runs

            performanceTestRunner.doRun(maxDuration, data, onComplete)
            onTick = slot.captured
        }

        @Test
        fun `should stop the clock and call the onComplete handler if the max duration has been reached`() {
            onTick(maxDuration + 1)

            verify { clock.stop() }
            verify { onComplete() }
            verify(exactly = 0) { dataPoster.post(any()) }
        }

        @Test
        fun `should stop the clock and call the onComplete handler if the data progression has finished`() {
            every { timeSeriesDataProgression.hasFinished() } returns true

            onTick(maxDuration)

            verify { clock.stop() }
            verify { onComplete() }
            verify(exactly = 0) { dataPoster.post(any()) }
        }

        @Test
        fun `should send all elapsed data items to the data poster`() {
            every { timeSeriesDataProgression.hasFinished() } returns false
            every { timeSeriesDataProgression.allMessagesUpTo(maxDuration) } returns
                    listOf(aDataPoint(1), aDataPoint(2))

            onTick(maxDuration)

            verify(exactly = 0) { clock.stop() }
            verify(exactly = 0) { onComplete() }
            verify {
                dataPoster.post("1")
                dataPoster.post("2")
            }
        }
    }
}