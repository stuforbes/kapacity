package com.stuforbes.kapacity.model

/**
 * Represents a single test scenario
 *
 * @param id The primary key of the scenario
 * @param dataPoints All data points relating to this scenario
 */
data class Scenario<Id, T>(val id: Id, val dataPoints: List<TimeSeriesData<T>>)

/**
 * A data point, which contains a time component in addition to it's constituent data
 *
 * @param time The time at which this data is relevant
 * @param data The actual data item
 */
data class TimeSeriesData<T>(val time: Long, val data: T)