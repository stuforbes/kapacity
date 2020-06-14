package com.stuforbes.kapacity.result

import java.io.File
import java.io.OutputStream

/**
 * Marker interface to handle formatting of results. Implementations of the invoke function
 * will receive the Result, and format it to a string
 *
 * @param Result the test result type
 */
interface ResultFormatter<Result> {

    /**
     * Format the result to a string representation
     *
     * @param result the test result
     * @return a String representation of the result
     */
    operator fun invoke(result: Result): String
}

/**
 * Print the formatted result to an output stream
 *
 * @param outputStream target for results to be printed
 * @param resultFormatter formats a Result instance to string
 */
open class ResultPrinter<Result>(
    private val outputStream: OutputStream,
    private val resultFormatter: ResultFormatter<Result>
) {

    /**
     * Print the result to the output stream
     *
     * @param result The test result
     */
    fun print(result: Result) {
        outputStream.write(result.formatted())
    }

    private fun Result.formatted() = resultFormatter(this).toByteArray()
}

fun <Result> consoleResultPrinter(resultFormatter: ResultFormatter<Result>) =
    ResultPrinter(System.out, object : ResultFormatter<Result> {
        override fun invoke(result: Result) = "\n\n\n${resultFormatter(result)}"
    })

fun <Result> fileResultPrinter(filepath: String, resultFormatter: ResultFormatter<Result>) =
    ResultPrinter(filepath.fileOutputStream(), resultFormatter)

private fun String.fileOutputStream() = File(this)
    .outputStream()