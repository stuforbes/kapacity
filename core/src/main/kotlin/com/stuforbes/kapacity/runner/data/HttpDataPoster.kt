@file:Suppress("UNCHECKED_CAST", "unused")

package com.stuforbes.kapacity.runner.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.stuforbes.kapacity.configuration.stringConfig
import com.stuforbes.kapacity.model.TimeSeriesData
import com.stuforbes.kapacity.recorder.DataPointRecordable
import com.stuforbes.kapacity.recorder.DataPointRecorder
import com.stuforbes.kapacity.util.Loggable
import io.github.rybalkinsd.kohttp.dsl.async.httpPostAsync
import io.github.rybalkinsd.kohttp.dsl.context.HttpPostContext
import io.github.rybalkinsd.kohttp.ext.url
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Response

typealias HttpPoster = (HttpPostContext.() -> Unit) -> Deferred<Response>

/**
 * Implementation of {@link DataPoster} that posts the data using an HTTP POST
 *
 * @param theUrl The url to post to
 * @param httpPoster To post the data
 */
class HttpDataPoster<T>(
    private val theUrl: String,
    private val httpPoster: HttpPoster
) : DataPoster<T>, DataPointRecordable<Int>, Loggable {

    private var dataPointRecorder: DataPointRecorder<Int>? = null

    constructor() : this(
        "http.post.url".stringConfig(),
        { httpPostAsync(init = it) }
    )

    override fun registerDataPointRecorder(dataPointRecorder: DataPointRecorder<Int>){
        this.dataPointRecorder = dataPointRecorder
    }

    override fun post(data: TimeSeriesData<T>) {
        debug { "Sending data ${data.data} via $theUrl" }
        GlobalScope.launch {
            val onCompletion = dataPointRecorder?.recordDataPoint()
            val response = httpPoster {
                url(theUrl)
                body {
                    json(data.data.asJson())
                }
            }

            val result = response.await().code()
            debug("Response: $result")
            onCompletion?.invoke(result)
        }
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()

        private fun <T> T.asJson() = objectMapper.writeValueAsString(this)
    }
}