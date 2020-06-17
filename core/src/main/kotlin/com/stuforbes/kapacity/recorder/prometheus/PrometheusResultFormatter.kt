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