package com.stuforbes.kapacity.test

import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.model.TimeSeriesData

fun aScenario(
    id: Int = arbitraryValue(),
    vararg dataPoints: TimeSeriesData<String>
) = Scenario(id, dataPoints.toList())

fun someScenarios(num: Int) = (1..num).map { aScenario(it) }

fun someDataPoints(vararg nums: Int) = nums.map { aDataPoint(it) }
fun aDataPoint(num: Int = arbitraryValue(), data: String = num.toString()) = TimeSeriesData(num.toLong(), data)
