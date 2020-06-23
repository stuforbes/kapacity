package com.stuforbes.kapacity.sample.data

import com.stuforbes.kapacity.configuration.intConfig
import com.stuforbes.kapacity.configuration.longConfig
import com.stuforbes.kapacity.configuration.longConfigOrNull
import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.model.TimeSeriesData
import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.util.Loggable
import kotlin.random.Random

data class Request(
    val messageId: Long,
    val response: ResponseAdvice
)

data class ResponseAdvice(
    val wait: Long,
    val code: Int,
    val body: String
)

class SampleDataLoader(
    private val numberOfScenarios: Int,
    private val numberOfMessagesPerScenario: Int,
    private val delayPerMessageMs: Long,
    private val seed: Long
) : DataLoader<Long, Request>, Loggable {

    private val random = Random(seed)

    constructor() : this(
        "num.scenarios".intConfig(),
        "num.messages.per.scenario".intConfig(),
        "delay.per.message".longConfig(),
        firstNonNullFrom({ "random.data.seed".longConfigOrNull() }, { System.currentTimeMillis() })!!
    )

    override fun loadData(): List<Scenario<Long, Request>> {
        info { "Loading random data with seed $seed" }
        return (1..numberOfScenarios).map { scenarioNum ->
            Scenario<Long, Request>(
                scenarioNum.toLong(),
                (1..numberOfMessagesPerScenario).map { messageNum ->
                    TimeSeriesData<Request>(
                        messageNum * delayPerMessageMs,
                        aRandomRequest()
                    )
                }
            )
        }.toList().also {
            println("Generated ${it.size}")
        }
    }

    private fun aRandomRequest() = Request(
        messageId = aRandomLong(),
        response = ResponseAdvice(
            wait = aRandomLong(max = LONGEST_WAIT_MS),
            code = RESPONSE_CODES.random(),
            body = aRandomString()
        )
    )

    private fun aRandomLong(
        min: Long = 0,
        max: Long = Long.MAX_VALUE
    ) = random.nextLong(min, max)

    private fun aRandomString() = "str-${aRandomLong()}"

    companion object {
        private const val LONGEST_WAIT_MS = 3000L

        private val RESPONSE_CODES = listOf(200, 301, 302, 400, 404, 500)

        private fun <T> firstNonNullFrom(vararg ops: () -> T?): T? = ops.fold(null as T?) { currentOrNull, op ->
            currentOrNull ?: op()
        }
    }
}
