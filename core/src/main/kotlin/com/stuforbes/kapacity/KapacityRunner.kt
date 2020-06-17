/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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