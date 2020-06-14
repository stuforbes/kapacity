package com.stuforbes.kapacity

import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.engine.Engine
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.result.ResultFormatter
import com.stuforbes.kapacity.result.ResultPrinter
import com.stuforbes.kapacity.result.consoleResultPrinter
import com.stuforbes.kapacity.runner.PerformanceTestRunner
import com.stuforbes.kapacity.runner.clock.Clock
import com.stuforbes.kapacity.runner.data.DataPoster

object KapacityRunner {

    private fun <Result> defaultResultFormatter() = object : ResultFormatter<Result>{
        override fun invoke(result: Result) = result.toString()
    }

    fun <Id, Data, Result> run(
        dataLoader: DataLoader<Id, Data>,
        dataSorter: TimeSeriesDataSorter<Data>,
        dataPoster: DataPoster<Data>,
        flightRecorder: FlightRecorder<Result>,
        resultPrinter: ResultPrinter<Result>?,
        resultFormatter: ResultFormatter<Result>?
    ): Engine<Id, Data, Result> {
        val printer = resultPrinter ?: consoleResultPrinter(
            resultFormatter ?: defaultResultFormatter()
        )

        val performanceTestRunner = PerformanceTestRunner(
            { Clock() },
            dataPoster
        )
        return Engine(dataLoader, dataSorter, performanceTestRunner, flightRecorder, printer)
    }
}