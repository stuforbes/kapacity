/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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