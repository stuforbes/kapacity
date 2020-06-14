package com.stuforbes.kapacity

import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.engine.Engine
import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.model.TimeSeriesData
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.result.ResultPrinter
import com.stuforbes.kapacity.runner.PerformanceTestRunner
import com.stuforbes.kapacity.test.aScenario
import com.stuforbes.kapacity.test.arbitraryValue
import com.stuforbes.kapacity.test.someDataPoints
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class EngineTest {

    @MockK
    private lateinit var dataLoader: DataLoader<Int, String>

    @MockK
    private lateinit var dataSorter: TimeSeriesDataSorter<String>

    @RelaxedMockK
    private lateinit var testRunner: PerformanceTestRunner<String>

    @RelaxedMockK
    private lateinit var flightRecorder: FlightRecorder<String>

    @RelaxedMockK
    private lateinit var resultPrinter: ResultPrinter<String>

    private lateinit var data: List<Scenario<Int, String>>

    private lateinit var sortedData: List<TimeSeriesData<String>>

    @InjectMockKs
    private lateinit var engine: Engine<Int, String, String>

    @BeforeEach
    fun before() {
        this.data = listOf(
            aScenario(1, *someDataPoints(1, 4, 7).toTypedArray()),
            aScenario(2, *someDataPoints(2, 5, 8).toTypedArray()),
            aScenario(3, *someDataPoints(3, 6, 9).toTypedArray())
        )

        this.sortedData = someDataPoints(1, 2, 3, 4, 5, 6, 7, 8, 9)

        every { dataLoader.loadData() } returns data
        every { dataSorter.sort(any()) } returns sortedData
    }

    @Test
    fun `starting the engine should load and sort the data, start the flight recorder, start the test runner and stop the flight recorder`() {
        engine.start(DURATION)

        verify {
            dataLoader.loadData()
            dataSorter.sort(data)
            flightRecorder.start()
            testRunner.doRun(DURATION, sortedData, any())
        }
    }

    @Test
    fun `the onCompletion handler passed to the testRunner should stop the flightRecorder and print the results on completion`() {
        val result = "a-result"
        val slot = slot<()->Unit>()
        every { testRunner.doRun(DURATION, sortedData, capture(slot)) } just Runs

        every { flightRecorder.stop() } returns result

        engine.start(DURATION)

        slot.captured()
        verify {
            flightRecorder.stop()
            resultPrinter.print(result)
        }
    }

    companion object {
        private val DURATION = arbitraryValue<Long>()
    }
}