package com.stuforbes.kapacity.runner

import com.stuforbes.kapacity.model.TimeSeriesData
import java.util.concurrent.atomic.AtomicInteger

/**
 * Map a list of {@link TimeSeriesData} to a {@link TimeSeriesDataProgression}
 */
fun <T> List<TimeSeriesData<T>>.asProgression() =
    TimeSeriesDataProgression(this)

private typealias DataAtTime<T> = Pair<Long, Set<TimeSeriesData<T>>>

/**
 * Used to progress through a list of {@link TimeSeriesData}. When calling allMessagesUpTo(), all data
 * items with a time lower than the specified time will be returned, but only if they have not been returned
 * in a previous call.
 *
 * This means that all data items will only ever be returned at most once.
 *
 * @param data All data items to be progressed through.
 */
class TimeSeriesDataProgression<T>(data: List<TimeSeriesData<T>>) {
    private val atTime =
        initAtTimeList(data)
    private val position = AtomicInteger(0)

    /**
     * Return all data items that have not been returned previously, but have a time lower
     * than the specified time.
     *
     * @param time The latest time we are interested in
     * @return A list of data points that haven't been returned previously, but have a time that is lower than the
     * specified time
     */
    fun allMessagesUpTo(time: Long): List<TimeSeriesData<T>> {
        val startPosition = position.get()
        return if (isWithinTime(startPosition, time)) {
            val endPosition = findEndPosition(startPosition, time)
            position.set(endPosition)

            atTime.allDataPointsBetween(startPosition, endPosition)

        } else emptyList()
    }

    /**
     * Returns true if all data items have been returned
     *
     * @return true if all data items have been returned, otherwise false
     */
    fun hasFinished() = position.get() >= atTime.size

    private fun isWithinTime(position: Int, time: Long) =
        atTime.size > position && atTime[position].first <= time

    private fun findEndPosition(startPosition: Int, time: Long): Int {
        var endPosition = startPosition
        while (isWithinTime(endPosition, time)) endPosition++
        return endPosition
    }

    private fun List<DataAtTime<T>>.allDataPointsBetween(startPosition: Int, endPosition: Int) =
        this.subList(startPosition, endPosition)
            .flatMap { it.second }

    companion object {
        private fun <T> initAtTimeList(data: List<TimeSeriesData<T>>): List<DataAtTime<T>> =
            data.groupBy { it.time }
                .toList()
                .sortedBy { it.first }
                .map { Pair(it.first, it.second.toSet()) }
    }
}
