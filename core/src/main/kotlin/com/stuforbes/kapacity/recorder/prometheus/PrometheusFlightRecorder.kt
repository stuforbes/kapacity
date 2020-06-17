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

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.recorder.prometheus.Prometheus.Companion.fromConfig
import com.stuforbes.kapacity.util.now
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Implementation of FlightRecorder that looks up results in Prometheus
 * @param latencyMetric Which metric in Prometheus to use
 * @param resolutionSeconds The 'step' parameter in the Prometheus call
 */
open class PrometheusFlightRecorder(
    private val latencyMetric: String,
    private val resolutionSeconds: Int
) : FlightRecorder<PrometheusResult> {

    private val prometheus = fromConfig()

    private var startTime: Long? = null

    override fun start() {
        startTime = now()
    }

    override fun stop(): PrometheusResult {
        val endTime = now()

        val prometheusUrl = generateLookupUrl(endTime)

        val response = httpGet {
            url(prometheusUrl)
        }

        return response.body()
            ?.let { objectMapper.readValue<PrometheusResult>(it.string()) }
            ?: throw IllegalStateException("Could not extract response body from Prometheus request")
    }

    private fun generateLookupUrl(endTime: Long) = startTime?.let { start ->
        with(prometheus) {
            "$protocol://$host:$port$endpoint" +
                    "?query=$latencyMetric&start=${start.formatTime()}&end=${endTime.formatTime()}&step=${resolutionSeconds}s"
        }
    } ?: throw IllegalStateException("Trying to fetch results before recording has started")

    companion object {
        private val objectMapper = jacksonObjectMapper()

        private fun Long.formatTime(): String {
            val instant = Instant.ofEpochMilli(this)
            val dateTime = instant.atZone(ZoneId.of("UTC")).toLocalDateTime()
            return dateTime.format()
        }

        private fun LocalDateTime.format() = "${this.format(DateTimeFormatter.ISO_DATE_TIME)}Z"
    }
}