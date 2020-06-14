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