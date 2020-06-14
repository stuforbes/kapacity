package com.stuforbes.kapacity.runner.data

import com.stuforbes.kapacity.test.aDataPoint
import com.stuforbes.kapacity.test.nextInt
import com.stuforbes.kapacity.test.nextString
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KafkaDataPosterTest {

    @RelaxedMockK
    private lateinit var kafkaProducer: KafkaProducer<String, String>

    private lateinit var topic: String

    private lateinit var kafkaDataPoster: KafkaDataPoster<String>

    @BeforeEach
    fun before() {
        this.topic = nextString()

        this.kafkaDataPoster = object : KafkaDataPoster<String>(topic, kafkaProducer){}
    }

    @Test
    fun `should serialise the data to json and send it via the KafkaProducer`() {
        val data = aDataPoint(nextInt())
        kafkaDataPoster.post(data)

        verify { kafkaProducer.send(ProducerRecord(topic, """"${data.data}"""")) }
    }
}