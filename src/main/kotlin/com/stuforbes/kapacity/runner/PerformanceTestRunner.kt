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
import com.stuforbes.kapacity.util.Loggable

/**
 * Posts data items at the correct point in time
 *
 * @param clockFactory Create a new {@link Clock} instance
 * @param dataPoster Post data items
 */
class PerformanceTestRunner<T>(
    private val clockFactory: () -> Clock,
    private val dataPoster: DataPoster<T>
) : Loggable {

    private lateinit var clock: Clock

    /**
     * Start a test run that will last until either:
     *   the maxDuration value is reached
     *   all data items have been posted
     * Once finished, invoke the onComplete callback function
     *
     * @param maxDuration The longest time the test run should last, in ms
     * @param data The chronologically sorted list of data points
     * @param onComplete callback to be invoked once the test run is complete
     */
    fun doRun(maxDuration: Long, data: List<TimeSeriesData<T>>, onComplete: ()->Unit) {
        debug { "Starting performance test run with a max duration of $maxDuration ms and ${data.size} data points" }
        val dataProgression = data.asProgression()

        clock = clockFactory().apply {
            start { time ->
                onTick(time, maxDuration, dataProgression, onComplete)
            }
        }
    }

    private fun onTick(
        time: Long,
        maxDuration: Long,
        dataProgression: TimeSeriesDataProgression<T>,
        onComplete: ()->Unit
    ) {
        trace { "onTick at time $time" }
        if (shouldStop(time, maxDuration, dataProgression)) {
            debug("Stopping clock now")
            clock.stop()
            onComplete()
        } else {
            val itemsToPost = allElapsedDataItems(time, dataProgression)
            debug("About to post ${itemsToPost.size} items")
            itemsToPost.forEach {
                dataPoster.post(it.data)
            }
        }
    }

    private fun allElapsedDataItems(
        time: Long,
        dataProgression: TimeSeriesDataProgression<T>
    ) = dataProgression.allMessagesUpTo(time)

    private fun shouldStop(currentTime: Long, maxDuration: Long, dataProgression: TimeSeriesDataProgression<T>) =
        currentTime > maxDuration || dataProgression.hasFinished()
}