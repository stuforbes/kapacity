package com.stuforbes.kapacity.sample.kafka

import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer as ApacheKafkaProducer

class KafkaProducerFactory(
    private val producerInitialiser: (String) -> KafkaProducer = { KafkaProducer(it) }
) {
    private val producerByTopicMap = mutableMapOf<String, KafkaProducer>()

    fun producerFor(topic: String) =
        producerByTopicMap.getOrPut(topic) { producerInitialiser(topic) }
}

class KafkaProducer(private val topic: String) {

    private val producer = ApacheKafkaProducer<String, String>(KAFKA_PROPS)

    fun post(message: String) {
        producer.send(ProducerRecord(topic, message))
    }

    companion object {
        val KAFKA_PROPS = Properties().apply {
            this[BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            this[CLIENT_ID_CONFIG] = "sample-app"
            this[KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
            this[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        }
    }
}