/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.recorder.prometheus

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class PrometheusResult(
    val status: String,
    val data: PrometheusResultData
)

data class PrometheusResultData(
    val resultType: String,
    val result: List<MetricResult>
)

data class MetricResult(
    val metric: Metric,
    val values: List<List<String>>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    val results: List<Pair<Long, Double>> = values.map {
        check(it.size == 2) { "Expected result row to contain 2 cells: $it" }

        Pair(
            it[0].split(".")[0].toLong(),
            it[1].toDouble()
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    val duration = results.last().first - results.first().first
}

data class Metric(
    @JsonAlias("__name__") val name: String,
    val instance: String,
    val job: String,
    val quantile: String
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    val percentile = (quantile.toDouble() * 100).toInt()
}