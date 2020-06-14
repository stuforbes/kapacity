package com.stuforbes.kapacity.sample.data

import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.model.TimeSeriesData

class SampleTimeSeriesDataSorter : TimeSeriesDataSorter<Request> {
    override fun sort(scenarios: List<List<TimeSeriesData<Request>>>) =
        scenarios
            .flatten()
            .sortedBy { it.time }
}