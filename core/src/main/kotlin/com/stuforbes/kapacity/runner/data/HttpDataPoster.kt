/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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