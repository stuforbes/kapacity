package com.stuforbes.kapacity.data

import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.model.TimeSeriesData

/**
 *  Flattens a list of scenarios into a list of data points, ordered chronologically
 */
interface TimeSeriesDataSorter<T> {
    /**
     * Sort the data points in the scenarios chronologically, and return the resulting flattened list
     *
     * @param scenarios A list of all scenarios
     * @return a list of ordered data points
     */
    fun sort(scenarios: List<Scenario<*, T>>): List<TimeSeriesData<T>>
}

/**
 * Default implementation of {@link TimeSeriesDataSorter}
 */
class StandardTimeSeriesDataSorter<T> : TimeSeriesDataSorter<T> {
    override fun sort(scenarios: List<Scenario<*, T>>) =
        scenarios
            .map { it.dataPoints }
            .flatten()
            .sortedBy { it.time }

}