package com.stuforbes.kapacity.recorder.prometheus

import com.stuforbes.kapacity.result.ResultFormatter
import java.math.RoundingMode.HALF_UP

/**
 * Format a PrometheusResult object into a readable String
 */
class PrometheusResultFormatter(
    private vararg val quantiles: String
) : ResultFormatter<PrometheusResult> {

    constructor() : this("0.5", "0.75", "0.95", "0.99")

    override fun invoke(result: PrometheusResult): String {
        val sortedResults = result.data.result
            .filter { quantiles.contains(it.metric.quantile) }
            .sortedBy { it.metric.quantile.toDouble() }

        return """
        |Results:
        |========
        |    Duration:                   ${sortedResults.head()?.duration?.div(1000L)} seconds
        |${sortedResults.map { metricResult ->
            "    ${metricResult.metric.percentile}th percentile:            ${metricResult.results.map { it.second }.average().to2DP()} ms"
        }.joinToString("\n")
        }
        |""".trimMargin()
    }

    companion object {
        private fun <T> List<T>.head() = this.getOrNull(0)

        private fun Double.to2DP() = this.toBigDecimal().setScale(2, HALF_UP)
    }
}