/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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