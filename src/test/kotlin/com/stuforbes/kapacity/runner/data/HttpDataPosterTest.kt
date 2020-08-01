/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@file:Suppress("DeferredResultUnused")

package com.stuforbes.kapacity.runner.data

import com.stuforbes.kapacity.recorder.DataPointRecorder
import com.stuforbes.kapacity.recorder.OnCompletion
import io.github.rybalkinsd.kohttp.dsl.context.BodyContext
import io.github.rybalkinsd.kohttp.dsl.context.HttpPostContext
import io.github.rybalkinsd.kohttp.ext.url
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.RequestBody
import okhttp3.Response
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class HttpDataPosterTest {

    @MockK
    private lateinit var httpPoster: HttpPoster

    @MockK
    private lateinit var deferredResponse: Deferred<Response>

    @MockK
    private lateinit var response: Response

    @MockK
    private lateinit var requestBody: RequestBody

    @MockK
    private lateinit var postContext: HttpPostContext

    @MockK
    private lateinit var bodyContext: BodyContext
    
    private lateinit var poster: HttpDataPoster<String>

    @BeforeEach
    fun before() {
        poster = HttpDataPoster(URL, httpPoster)

        coEvery { deferredResponse.await() } returns response
        every { response.code() } returns 200

        mockkStatic("io.github.rybalkinsd.kohttp.ext.HttpContextExtKt")
    }

    @Nested
    inner class HttpPosting {
        @Test
        fun `should call the http poster when posting data`() {
            every { httpPoster(any()) } returns deferredResponse
            runPost()
        }


        @Test
        fun `should set the url and body on the post context`() {
            val postContextSlot = primePostContext()

            runPost()

            checkPostContextPopulated(postContextSlot.captured)
        }

        @Test
        fun `should set the body to the json representation of the data on the body context`() {
            val postContextSlot = primePostContext()
            val bodyContextSlot = primeBodyContext()

            runPost()
            checkPostContextPopulated(postContextSlot.captured)
            checkBodyContextPopulated(bodyContextSlot.captured)
        }
    }

    @Nested
    inner class DataRecording {
        @MockK
        private lateinit var dataPointRecorder: DataPointRecorder<Int>

        @RelaxedMockK
        private lateinit var onCompletion: OnCompletion<Int>

        @BeforeEach
        fun before() {
            poster = HttpDataPoster(URL, httpPoster)
            poster.registerDataPointRecorder(dataPointRecorder)
        }

        @Test   
        fun `should start recording the data point, make the request, and record the response once finished`() = runBlocking{
            every { httpPoster(any()) } returns deferredResponse
            every { dataPointRecorder.recordDataPoint() } returns onCompletion
            every { response.code() } returns RESPONSE_CODE

            poster.post(DATA)
            delay(100)

            verifyOrder {
                dataPointRecorder.recordDataPoint()
                httpPoster(any())
                onCompletion(RESPONSE_CODE)
            }
        }
    }

    private fun runPost() = runBlocking {
        poster.post(DATA)
        delay(100)
        verify { httpPoster(any()) }
    }

    private fun checkPostContextPopulated(postContextInit: HttpPostContext.()->Unit){
        postContext.apply(postContextInit)

        verify {
            postContext.url(URL)
            postContext.body(null, any())
        }
    }

    private fun checkBodyContextPopulated(bodyContextInit: BodyContext.() -> RequestBody) {
        bodyContext.bodyContextInit()

        verify { bodyContext.json(""""$DATA"""") }
    }

    private fun primePostContext(): CapturingSlot<HttpPostContext.() -> Unit> {
        val postContextSlot = slot<HttpPostContext.() -> Unit>()

        every { httpPoster(capture(postContextSlot)) } returns deferredResponse
        every { postContext.body(null, any())} just Runs
        every { postContext.url(any<String>()) } just Runs

        return postContextSlot
    }

    private fun primeBodyContext(): CapturingSlot<BodyContext.() -> RequestBody> {
        val bodyContextSlot = slot<BodyContext.() -> RequestBody>()
        every { postContext.body(null, capture(bodyContextSlot))} just Runs

        every { bodyContext.json(any<String>())} returns requestBody

        return bodyContextSlot
    }

    companion object {
        private const val URL = "http://a-url.com"
        private const val RESPONSE_CODE = 204

        private const val DATA = "some data"
    }
}