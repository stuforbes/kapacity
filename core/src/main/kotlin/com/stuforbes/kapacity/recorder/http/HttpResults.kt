package com.stuforbes.kapacity.recorder.http

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

/**
 * Results object to print the durations of responses
 */
class HttpResults {
    /**
     * Response result object, containing the HttpResponse status code, and the duration of the request
     * @param status The Http Status Code
     * @param time The duration of the request
     */
    data class Response(val status: Int, val time: Long)

    private val responses = mutableListOf<Response>()

    /**
     * Record an Http Response status code and request duration
     */
    fun addResponse(statusCode: Int, time: Long) {
        responses.add(Response(statusCode, time))
    }

    override fun toString() = """
        |Results:
        |========
        |    Number of responses:    ${responses.size}
        |    Mean response time:     ${responses.meanResponseTime()} ms
        |    Max response time:      ${responses.percentileTime(100)} ms
        |    Median:                 ${responses.percentileTime(50)} ms
        |    75th percentile:        ${responses.percentileTime(75)} ms
        |    90th percentile:        ${responses.percentileTime(90)} ms
        |    95th percentile:        ${responses.percentileTime(95)} ms
        |    99th percentile:        ${responses.percentileTime(99)} ms
        |    
        |By HTTP status code:
        |====================
        |
        |${printResultsByStatus()}
    |""".trimMargin()

    private fun printResultsByStatus(): String {
        val responsesByStatus = responsesByStatus()
        return responsesByStatus.keys.sorted().joinToString("\n") { status ->
            val statusResponses = responsesByStatus[status] ?: error("Could not find responses for status $status")

            """
                    |$status:
                    |====
                    |   Number of responses:    ${statusResponses.size}
                    |   Proportion:             ${(statusResponses.size.toDouble() / responses.size.toDouble()).asPercentage()}%
                    |   Mean response time:     ${statusResponses.meanResponseTime()} ms
                    |   Max response time:      ${statusResponses.percentileTime(100)} ms
                    |   Median:                 ${statusResponses.percentileTime(50)} ms
                    |   75th percentile:        ${statusResponses.percentileTime(75)} ms
                    |   90th percentile:        ${statusResponses.percentileTime(90)} ms
                    |   95th percentile:        ${statusResponses.percentileTime(95)} ms
                    |   99th percentile:        ${statusResponses.percentileTime(99)} ms
                |""".trimMargin()
        }
    }

    private fun List<Response>.meanResponseTime() = toTimes().averageOrZero().to2DP()
    private fun List<Response>.percentileTime(p: Int) = toTimes().percentile(p)

    private fun responsesByStatus() = responses
        .groupBy { it.status }

    private fun List<Long>.percentile(p: Int): Long {
        return if (responses.isNotEmpty()) {
            val sorted = this
                .sortedBy { it }

            val index = ceil((p.toDouble() / 100) * sorted.size).toInt()

            return sorted[index - 1]
        } else 0L
    }

    companion object {
        private fun List<Response>.toTimes() = this.map { it.time }

        private fun List<Long>.averageOrZero() = when {
            isEmpty() -> 0.0
            else -> average()
        }

        private fun Double.to2DP(): Double = BigDecimal(this)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toDouble()

        private fun Double.asPercentage() = (this * 100).to2DP()
    }
}