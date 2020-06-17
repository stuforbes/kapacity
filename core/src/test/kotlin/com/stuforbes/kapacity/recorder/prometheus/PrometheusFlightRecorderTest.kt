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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.test.shouldEqual
import com.stuforbes.kapacity.util.primeNowWith
import io.github.rybalkinsd.kohttp.dsl.context.HttpGetContext
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class PrometheusFlightRecorderTest {

    @MockK
    private lateinit var response: Response

    private lateinit var flightRecorder: FlightRecorder<PrometheusResult>

    @BeforeEach
    fun before() {
        this.flightRecorder = PrometheusFlightRecorder(
            LATENCY_METRIC,
            RESOLUTION_SECONDS
        )

        mockkStatic("io.github.rybalkinsd.kohttp.dsl.HttpGetDslKt")
        every { httpGet(any(), any()) } returns response

        mockkStatic("io.github.rybalkinsd.kohttp.ext.HttpContextExtKt")

        val responseBody = mockk<ResponseBody>()
        every { response.body() } returns responseBody
        every { responseBody.string() } returns PROMETHEUS_RESULT_JSON
    }

    @Test
    fun `throws an exception if trying to generate data before recording has started`() {
        val ex = assertThrows<IllegalStateException> {
            flightRecorder.stop()
        }

        ex.message shouldEqual "Trying to fetch results before recording has started"
    }

    @Test
    fun `makes a valid call to Prometheus to get the correct result`() {
        primeNowWith(listOf(START_TIME, END_TIME)) {
            val httpGetInitSlot = slot<HttpGetContext.() -> Unit>()
            every { httpGet(any(), capture(httpGetInitSlot)) } returns response

            flightRecorder.start()
            flightRecorder.stop()

            val context = mockk<HttpGetContext>()
            val urlSlot = slot<String>()
            every { context.url(capture(urlSlot)) } just Runs
            context.apply(httpGetInitSlot.captured)

            urlSlot.captured shouldEqual
                    "http://prometheus:9090" +
                    "/api/v1/query_range" +
                    "?query=a.metric.name" +
                    "&start=2020-03-28T14:08:10Z" +
                    "&end=2020-03-28T15:08:10Z" +
                    "&step=100s"
        }
    }

    @Test
    fun `returns a valid result object`() {
        flightRecorder.start()
        val result = flightRecorder.stop()

        result shouldEqual PROMETHEUS_RESULT
    }

    @Test
    fun `throws an exception if the response body was null`() {
        every { response.body() } returns null

        flightRecorder.start()
        val ex = assertThrows<IllegalStateException> { flightRecorder.stop() }
        ex.message shouldEqual "Could not extract response body from Prometheus request"
    }

    companion object {
        private const val LATENCY_METRIC = "a.metric.name"
        private const val RESOLUTION_SECONDS = 100

        private const val START_TIME = 1585404490000L
        private const val END_TIME = START_TIME + 3_600_000L

        private val PROMETHEUS_RESULT = PrometheusResult(
            status = "success",
            data = PrometheusResultData(
                resultType = "matrix",
                result = listOf(
                    MetricResult(
                        metric = Metric(
                            name = "kafka_sample_latency",
                            instance = "dockerhost:8080",
                            job = "kapacity-sample",
                            quantile = "0.5"
                        ),
                        values = listOf(
                            listOf("1584194413.250", "1535.875"),
                            listOf("1584194428.250", "1727.875")
                        )
                    )
                )
            )
        )

        private val PROMETHEUS_RESULT_JSON = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .writeValueAsString(PROMETHEUS_RESULT)
    }
}