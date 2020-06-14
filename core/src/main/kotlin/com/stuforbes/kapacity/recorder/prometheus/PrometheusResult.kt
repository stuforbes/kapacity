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