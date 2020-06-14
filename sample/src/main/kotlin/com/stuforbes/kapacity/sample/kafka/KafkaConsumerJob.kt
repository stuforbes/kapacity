package com.stuforbes.kapacity.sample.kafka

import com.stuforbes.kapacity.sample.async.ClosableRunnable
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean

class KafkaConsumerJob(
    topic: String,
    private val onMessage: (String) -> Unit,
    private val pollFrequency: Long = DEFAULT_POLL_FREQUENCY
) : ClosableRunnable {

    private val consumer = KafkaConsumer<String, String>(KAFKA_PROPS).apply {
        subscribe(listOf(topic))
    }
    private val running = AtomicBoolean(false)

    override fun run() {
        logger.info("Starting Kafka consumer job")
        running.set(true)
        while (running.get()) {
            val records = consumer.poll(pollFrequency.asMillis())
            records.forEach { record ->
                record.value()?.let(onMessage)
            }
        }
        logger.info("Kafka consumer job has finished")
    }

    override fun close() {
        logger.info("About to close Kafka consumer job")
        running.set(false)
        consumer.close()
    }

    companion object {
        private const val DEFAULT_POLL_FREQUENCY = 100L

        private val logger = LoggerFactory.getLogger(KafkaConsumerJob::class.java)

        private fun Long.asMillis() = Duration.ofMillis(this)

        private val KAFKA_PROPS = Properties().apply {
            this[BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
            this[ENABLE_AUTO_COMMIT_CONFIG] = true
            this[GROUP_ID_CONFIG] = "sample-app"
            this[KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java.name
            this[VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
            this[MAX_POLL_RECORDS_CONFIG] = 1
            this[ENABLE_AUTO_COMMIT_CONFIG] = "false"
            this[AUTO_OFFSET_RESET_CONFIG] = "earliest"
        }
    }
}