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

import com.stuforbes.kapacity.recorder.http.HttpResults
import com.stuforbes.kapacity.test.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HttpResultsTest {

    private lateinit var results: HttpResults

    @BeforeEach
    fun before() {
        results = HttpResults()

        buildResponseList().forEach { results.addResponse(it.first, it.second) }
    }

    @Test
    fun `should print the results correctly`() {
        results.toString().trim() shouldEqual EXPECTED_RESULTS.trim()
    }

    companion object {
        private val EXPECTED_RESULTS = """
        |Results:
        |========
        |    Number of responses:    10
        |    Mean response time:     550.0 ms
        |    Max response time:      1000 ms
        |    Median:                 500 ms
        |    75th percentile:        800 ms
        |    90th percentile:        900 ms
        |    95th percentile:        1000 ms
        |    99th percentile:        1000 ms
        |    
        |By HTTP status code:
        |====================
        |
        |200:
        |====
        |   Number of responses:    3
        |   Proportion:             30.0%
        |   Mean response time:     200.0 ms
        |   Max response time:      300 ms
        |   Median:                 200 ms
        |   75th percentile:        300 ms
        |   90th percentile:        300 ms
        |   95th percentile:        300 ms
        |   99th percentile:        300 ms
        |
        |204:
        |====
        |   Number of responses:    1
        |   Proportion:             10.0%
        |   Mean response time:     400.0 ms
        |   Max response time:      400 ms
        |   Median:                 400 ms
        |   75th percentile:        400 ms
        |   90th percentile:        400 ms
        |   95th percentile:        400 ms
        |   99th percentile:        400 ms
        |
        |301:
        |====
        |   Number of responses:    2
        |   Proportion:             20.0%
        |   Mean response time:     550.0 ms
        |   Max response time:      600 ms
        |   Median:                 500 ms
        |   75th percentile:        600 ms
        |   90th percentile:        600 ms
        |   95th percentile:        600 ms
        |   99th percentile:        600 ms
        |
        |400:
        |====
        |   Number of responses:    2
        |   Proportion:             20.0%
        |   Mean response time:     750.0 ms
        |   Max response time:      800 ms
        |   Median:                 700 ms
        |   75th percentile:        800 ms
        |   90th percentile:        800 ms
        |   95th percentile:        800 ms
        |   99th percentile:        800 ms
        |
        |404:
        |====
        |   Number of responses:    1
        |   Proportion:             10.0%
        |   Mean response time:     900.0 ms
        |   Max response time:      900 ms
        |   Median:                 900 ms
        |   75th percentile:        900 ms
        |   90th percentile:        900 ms
        |   95th percentile:        900 ms
        |   99th percentile:        900 ms
        |
        |500:
        |====
        |   Number of responses:    1
        |   Proportion:             10.0%
        |   Mean response time:     1000.0 ms
        |   Max response time:      1000 ms
        |   Median:                 1000 ms
        |   75th percentile:        1000 ms
        |   90th percentile:        1000 ms
        |   95th percentile:        1000 ms
        |   99th percentile:        1000 ms
        |""".trimMargin()

        private fun buildResponseList(): List<Pair<Int, Long>> = (1..10).map {
            val code = when (it) {
                1, 2, 3 -> 200
                4 -> 204
                5, 6 -> 301
                7, 8 -> 400
                9 -> 404
                else -> 500
            }
            code to (it * 100).toLong()
        }
    }
}