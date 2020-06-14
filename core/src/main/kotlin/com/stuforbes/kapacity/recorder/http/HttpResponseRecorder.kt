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