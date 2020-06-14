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
                dataPoster.post(it)
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