package com.stuforbes.kapacity.sample.data

import com.stuforbes.kapacity.runner.data.KafkaDataPoster
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class KafkaDataPosterImpl : KafkaDataPoster<Request>(
    "ReceiveData",
    KafkaProducer(KAFKA_PROPS)
) {
    companion object {
        val KAFKA_PROPS = Properties().apply {
            this[BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            this[CLIENT_ID_CONFIG] = "sample-app"
            this[KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
            this[VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        }
    }
}