@file:Suppress("UNCHECKED_CAST")

package com.stuforbes.kapacity.result

import com.stuforbes.kapacity.test.fieldValueNamed
import com.stuforbes.kapacity.test.nextInt
import com.stuforbes.kapacity.test.nextString
import com.stuforbes.kapacity.test.shouldEqual
import com.stuforbes.kapacity.test.shouldHaveAFieldValueOf
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.io.OutputStream

@ExtendWith(MockKExtension::class)
internal class ResultPrinterTest {

    @RelaxedMockK
    private lateinit var outputStream: OutputStream

    @MockK
    private lateinit var resultFormatter: ResultFormatter<Int>

    @InjectMockKs
    private lateinit var resultPrinter: ResultPrinter<Int>

    @Test
    fun `formats the result and writes it to the output stream`() {
        val result = nextInt()
        val formattedResult = nextString()

        every { resultFormatter(result) } returns formattedResult

        resultPrinter.print(result)

        verify { outputStream.write(formattedResult.toByteArray()) }
    }
}

@ExtendWith(MockKExtension::class)
internal class ConsoleResultPrinterTest {

    @MockK
    private lateinit var resultFormatter: ResultFormatter<Int>

    @Test
    fun `should create a result printer with the System out outputStream`() {
        val resultPrinter = consoleResultPrinter(resultFormatter)

        resultPrinter.shouldHaveAFieldValueOf("outputStream", System.out)
    }

    @Test
    fun `should print the result with suitable padding`() {
        val formattedResult = "24"
        every { resultFormatter(any()) } returns formattedResult

        val resultPrinter = consoleResultPrinter(resultFormatter)
        val paddingResultFormatter = resultPrinter.fieldValueNamed<ResultFormatter<Int>>("resultFormatter")

        paddingResultFormatter(24) shouldEqual "\n\n\n$formattedResult"
    }
}

@ExtendWith(MockKExtension::class)
internal class FileResultPrinterTest {

    @MockK
    private lateinit var resultFormatter: ResultFormatter<String>

    @Test
    fun `should create a result printer with an output stream to the specified file`() {
        val file = File.createTempFile("fileresultprinter", "out")
        file.deleteOnExit()

        val result = "some result"
        val formattedResult = "some formatted result"
        every { resultFormatter(result) } returns formattedResult

        val resultPrinter = fileResultPrinter(file.absolutePath, resultFormatter)
        resultPrinter.print(result)

        file.readText() shouldEqual formattedResult
    }
}