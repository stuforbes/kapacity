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