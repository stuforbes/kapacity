/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("SameParameterValue")

package com.stuforbes.kapacity.recorder.prometheus

import com.stuforbes.kapacity.result.ResultFormatter
import com.stuforbes.kapacity.test.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PrometheusResultFormatterTest {

    private lateinit var resultFormatter: ResultFormatter<PrometheusResult>

    @BeforeEach
    fun before() {
        this.resultFormatter = PrometheusResultFormatter("0.8", "0.9", "0.99")
    }

    @Test
    fun `should print output for PrometheusResult correctly`() {

        val quantileResults = listOf(
            Triple("0.8", 1000L, 1050L),
            Triple("0.9", 1800L, 1900L),
            Triple("0.99", 2000L, 2222L)
        )

        val formattedMessage = resultFormatter(aResultFor(System.currentTimeMillis(), 60_000L, quantileResults))

        formattedMessage shouldEqual EXPECTED_RESULTS
    }

    companion object {

        private val EXPECTED_RESULTS = """
            |Results:
            |========
            |    Duration:                   60 seconds
            |    80th percentile:            1025.00 ms
            |    90th percentile:            1850.00 ms
            |    99th percentile:            2111.00 ms
            |""".trimMargin()

        private fun aResultFor(startTime: Long, duration: Long, quantileResults: List<Triple<String, Long, Long>>) =
            PrometheusResult(
                status = "success",
                data = PrometheusResultData(
                    resultType = "matrix",
                    result = quantileResults.map {
                        metricResultFor(it, startTime, duration)
                    }
                )
            )

        private fun metricResultFor(
            quantileResult: Triple<String, Long, Long>,
            startTime: Long,
            duration: Long
        ) = MetricResult(
            Metric(
                name = "metric.name",
                instance = "instance",
                job = "job",
                quantile = quantileResult.first
            ),
            values = listOf(
                listOf(startTime.toString(), quantileResult.second.toString()),
                listOf((startTime + duration).toString(), quantileResult.third.toString())
            )
        )
    }
}