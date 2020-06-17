/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.runner.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.stuforbes.kapacity.model.TimeSeriesData
import com.stuforbes.kapacity.util.Loggable
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

/**
 * Implementation of {@link DataPoster} that posts the data via Kafka
 *
 * @param topic The topic to post to
 * @param Kafka KafkaProducer instance
 */
abstract class KafkaDataPoster<T>(
    private val topic: String,
    private val kafka: KafkaProducer<String, String>
) : DataPoster<T>, Loggable {

    override fun post(data: TimeSeriesData<T>) {
        debug { "Sending data ${data.data} via $topic" }
        kafka.send(ProducerRecord(topic, data.data.asJson()))
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()

        private fun <T> T.asJson() = objectMapper.writeValueAsString(this)
    }
}