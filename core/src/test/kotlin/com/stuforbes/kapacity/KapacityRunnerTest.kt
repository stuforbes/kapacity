package com.stuforbes.kapacity

import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.result.ResultFormatter
import com.stuforbes.kapacity.result.ResultPrinter
import com.stuforbes.kapacity.runner.data.DataPoster
import com.stuforbes.kapacity.test.fieldValueNamed
import com.stuforbes.kapacity.test.shouldHaveAFieldValueOf
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KapacityRunnerTest {

    @MockK
    private lateinit var dataLoader: DataLoader<Int, String>

    @MockK
    private lateinit var dataSorter: TimeSeriesDataSorter<String>

    @MockK
    private lateinit var flightRecorder: FlightRecorder<String>

    @MockK
    private lateinit var resultPrinter: ResultPrinter<String>

    @MockK
    private lateinit var resultFormatter: ResultFormatter<String>

    @MockK
    private lateinit var dataPoster: DataPoster<String>

    @Test
    fun `loads the required services and instantiates an engine`() {
        val engine = KapacityRunner.run(
            dataLoader,
            dataSorter,
            dataPoster,
            flightRecorder,
            resultPrinter,
            null
        )

        engine.shouldHaveAFieldValueOf("dataLoader", dataLoader)
        engine.shouldHaveAFieldValueOf("dataSorter", dataSorter)
        engine.shouldHaveAFieldValueOf("flightRecorder", flightRecorder)
        engine.shouldHaveAFieldValueOf("resultPrinter", resultPrinter)
    }

    @Test
    fun `if the result printer is null, but there is a result formatter, uses a console printer`() {
        val engine = KapacityRunner.run(
            dataLoader,
            dataSorter,
            dataPoster,
            flightRecorder,
            null,
            resultFormatter
        )

        val actualResultPrinter = engine.fieldValueNamed<ResultPrinter<String>>("resultPrinter")
        actualResultPrinter.shouldHaveAFieldValueOf("outputStream", System.out)
    }
}