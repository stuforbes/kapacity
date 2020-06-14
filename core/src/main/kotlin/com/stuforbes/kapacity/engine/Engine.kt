package com.stuforbes.kapacity.engine

import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.result.ResultPrinter
import com.stuforbes.kapacity.runner.PerformanceTestRunner
import com.stuforbes.kapacity.util.Loggable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Main Execution logic of Kapacity. When started, will perform the following actions:
 * 1. load and sort the scenarios
 * 2. beginning any recording
 * 3. start data posting
 * 4. stop recording
 * 5. print results
 */
class Engine<Id, T, Result>(
    private val dataLoader: DataLoader<Id, T>,
    private val dataSorter: TimeSeriesDataSorter<T>,
    private val testRunner: PerformanceTestRunner<T>,
    private val flightRecorder: FlightRecorder<Result>,
    private val resultPrinter: ResultPrinter<Result>
) : Loggable {

    /**
     * Start the test run for the specified duration
     *
     * @param duration Length of test, in ms
     */
    fun start(duration: Long) {
        info { "Starting" }

        debug { "Loading data" }
        val scenarios = dataLoader.loadData()
        debug { "Sorting data" }
        val sortedDataPoints = dataSorter.sort(scenarios)

        debug { "Starting performance recording" }
        startRecording()
        debug { "Beginning data playback" }
        testRunner.doRun(duration, sortedDataPoints) {
            debug { "Data playback complete" }
            val result = stopRecording()
            resultPrinter.print(result)
        }
    }

    private fun startRecording() {
        GlobalScope.launch {
            flightRecorder.start()
        }
    }

    private fun stopRecording() = flightRecorder.stop()
}

