package com.stuforbes.kapacity.sample

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.stuforbes.kapacity.sample.async.BackgroundOperation
import com.stuforbes.kapacity.sample.kafka.KafkaConsumerJob
import com.stuforbes.kapacity.sample.kafka.KafkaProducerFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig.DEFAULT
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.slf4j.LoggerFactory
import java.time.Duration.ofMillis
import java.time.Duration.ofSeconds
import kotlin.system.measureTimeMillis

data class Request(
    val messageId: Long,
    val response: ResponseAdvice
)

data class ResponseAdvice(
    val wait: Long,
    val code: Int,
    val body: String
)

private val meterRegistry = PrometheusMeterRegistry(DEFAULT)
private val objectMapper = jacksonObjectMapper()

val httpLatency: DistributionSummary = DistributionSummary.builder("kapacity.sample.latency")
    .publishPercentiles(0.5, 0.95) // median and 95th percentile
    .publishPercentileHistogram()
//    .sla(ofMillis(100))
//    .minimumExpectedValue(1)
//    .maximumExpectedValue(10)
    .register(meterRegistry)

val kafkaLatency: DistributionSummary = DistributionSummary.builder("kapacity.sample.latency")
    .publishPercentiles(0.5, 0.95, 0.99)
    .publishPercentileHistogram()
    .register(meterRegistry)

@ObsoleteCoroutinesApi
fun Application.main() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    install(MicrometerMetrics) {
        registry = meterRegistry

        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .maximumExpectedValue(ofSeconds(20).toNanos())
            .sla(
                ofMillis(100).toNanos(),
                ofMillis(500).toNanos()
            )
            .build()
    }
    install(BackgroundOperation) {
        val producer = KafkaProducerFactory().producerFor("PostData")
        val context = newFixedThreadPoolContext(10, "Kafka Processors")
        job = KafkaConsumerJob("ReceiveData", { message ->
            CoroutineScope(context).launch {
                parseRequestOrNull(message)?.let { request ->
                    handleRequestAndRecordTime(request, "kafka", kafkaLatency) { _, body ->
                        producer.post(body)
                    }
                }
            }
        })
    }

    routing {
        get("/metrics") {
            call.respond(meterRegistry.scrape())
        }
        post("/") {
            val request = call.receive<Request>()
            handleRequestAndRecordTime(request, "http", httpLatency) { code, body ->
                call.respond(code.asStatusCode(), body)
            }
        }
    }
}

private val LOGGER = LoggerFactory.getLogger("Main")

private suspend fun handleRequestAndRecordTime(
    request: Request,
    transport: String,
    latencyRecorder: DistributionSummary,
    postResponse: suspend (Int, String) -> Unit
) {
    val duration = measureTimeMillis {
        LOGGER.info("Received request {} via {}", request.messageId, transport)

        with(request.response) {
            LOGGER.info("Waiting for $wait ms")
            delay(wait)

            LOGGER.info("Responding with {}: {} for message {}", code, body, request.messageId)
            postResponse(code, body)
        }
    }
    LOGGER.info("Recording latency of $duration")
    latencyRecorder.record(duration.toDouble())
}

private fun parseRequestOrNull(str: String) = try {
    objectMapper.readValue<Request>(str)
} catch (ex: JsonParseException) {
    LOGGER.warn("Could not deserialise request: $str", ex)
    null
}

private fun Int.asStatusCode() = HttpStatusCode(this, "")