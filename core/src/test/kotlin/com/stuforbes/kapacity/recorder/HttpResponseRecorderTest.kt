/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.recorder

import com.stuforbes.kapacity.recorder.http.HttpResponseRecorder
import com.stuforbes.kapacity.test.shouldContain
import com.stuforbes.kapacity.util.primeNowWith
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class HttpResponseRecorderTest {

    @InjectMockKs
    private lateinit var httpResponseRecorder: HttpResponseRecorder

    @BeforeEach
    fun before() {
        httpResponseRecorder.start()
    }

    @Test
    fun `stopping before recording any data points returns an empty results object`() {
        val results = httpResponseRecorder.stop()

        results.toString() shouldContain "Number of responses:    0"
    }

    @Test
    fun `recording data points should add new entries to the results`() {
        primeNowWith(listOf(1L, 2L, 3L, 5L)) {
            httpResponseRecorder.recordDataPoint().invoke(200)
            httpResponseRecorder.recordDataPoint().invoke(404)

            val results = httpResponseRecorder.stop()

            results.toString() shouldContain "200:\n====\n   Number of responses:    1"
            results.toString() shouldContain "404:\n====\n   Number of responses:    1"
        }
    }
}