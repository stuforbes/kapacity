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