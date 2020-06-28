/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.recorder.http

import com.stuforbes.kapacity.recorder.DataPointRecordingFlightRecorder
import com.stuforbes.kapacity.recorder.OnCompletion
import com.stuforbes.kapacity.util.Loggable
import com.stuforbes.kapacity.util.now
import com.stuforbes.kapacity.util.waitUntil
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicLong


/**
 * Records Http Responses, and returns the results in an HttpResults object
 */
class HttpResponseRecorder: DataPointRecordingFlightRecorder<Int, HttpResults>, Loggable {

    private val inFlightRequests = AtomicLong(0)

    private val results = HttpResults()

    override fun start() {
        info { "Starting Response recorder" }
    }

    override fun stop(): HttpResults {
        info { "Recording complete" }
        runBlocking { waitUntil(MAX_WAIT) { inFlightRequests.get() == 0L } }
        return results
    }

    override fun recordDataPoint(): OnCompletion<Int> {
        val start = now()
        inFlightRequests.incrementAndGet()
        return {
            val end = now()
            results.addResponse(it, end - start)
            inFlightRequests.decrementAndGet()
        }
    }

    companion object {
        private const val MAX_WAIT = 10000L
    }
}