package com.stuforbes.kapacity.console

import com.stuforbes.kapacity.KapacityRunner
import com.stuforbes.kapacity.data.DataLoader
import com.stuforbes.kapacity.data.TimeSeriesDataSorter
import com.stuforbes.kapacity.recorder.FlightRecorder
import com.stuforbes.kapacity.result.ResultFormatter
import com.stuforbes.kapacity.result.ResultPrinter
import com.stuforbes.kapacity.runner.data.DataPoster
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

/**
 * Start the Kapacity runner from the command line.
 * The following arguments must be supplied:
 *   -dl: An implementation of com.stuforbes.kapacity.data.DataLoader
 *   -ds: An implementation of com.stuforbes.kapacity.data.TimeSeriesDataSorter
 *   -dp: An implementation of com.stuforbes.kapacity.runner.data.DataPoster
 *   -fr: An implementation of com.stuforbes.kapacity.recorder.FlightRecorder
 *   And one of:
 *      -rp: An implementation of com.stuforbes.kapacity.result.ResultPrinter
 *      -rf: An implementation of com.stuforbes.kapacity.result.ResultFormatter
 */
fun main(args: Array<String>) {

    println("Args: ${args.toList()}")
    
    val parser = DefaultParser()
    val options = buildOptions()
    val cmd = parser.parse(options, args)

    cmd.ifValid {
        KapacityRunner.run<Any, Any, Any>(
            cmd.getOptionValue("dl").instantiate(),
            cmd.getOptionValue("ds").instantiate(),
            cmd.getOptionValue("dp").instantiate(),
            cmd.getOptionValue("fr").instantiate(),
            cmd.getOptionValue("rp")?.instantiate(),
            cmd.getOptionValue("rf")?.instantiate()
        ).start(cmd.getOptionValue("t").toLong())
    }
}

private fun buildOptions() = Options().apply {
    addRequiredOption("t", "time", true, "The test duration in ms")
    addRequiredOption("dl", "dataLoader", true, "The implementation of ${DataLoader::class.qualifiedName}")
    addRequiredOption("ds", "dataSorter", true, "The implementation of ${TimeSeriesDataSorter::class.qualifiedName}")
    addRequiredOption("dp", "dataPoster", true, "The implementation of ${DataPoster::class.qualifiedName}")
    addRequiredOption("fr", "flightRecorder", true, "The implementation of ${FlightRecorder::class.qualifiedName}")
    addOption("rp", "resultPrinter", true, "The implementation of ${ResultPrinter::class.qualifiedName}")
    addOption("rf", "resultFormatter", true, "The implementation of ${ResultFormatter::class.qualifiedName}")
}

private fun CommandLine.ifValid(op: () -> Unit) =
    if (hasOption("rp") && hasOption("rf"))
        System.err.println("Either rp or rf must be set, not both")
    else if (hasOption("rp").not() && hasOption("rf").not())
        System.err.println("One of rp or rf must be set")
    else if (getOptionValue("t").isNumeric().not() && getOptionValue("t").toLong() > 0)
        System.err.println("Duration must be a positive number")
    else op()

@Suppress("UNCHECKED_CAST")
private fun <T> String.instantiate(): T = Class.forName(this).newInstance() as T

private fun String.isNumeric() = try {
    this.toLong()
    true
} catch (ex: NumberFormatException) {
    false
}