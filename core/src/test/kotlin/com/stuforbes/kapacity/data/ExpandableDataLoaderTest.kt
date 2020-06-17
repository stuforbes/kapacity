/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.data

import com.stuforbes.kapacity.test.aDataPoint
import com.stuforbes.kapacity.test.aScenario
import com.stuforbes.kapacity.test.arbitraryValue
import com.stuforbes.kapacity.test.shouldEqual
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class ExpandableDataLoaderTest {

    @MockK
    private lateinit var delegateDataLoader: DataLoader<Int, String>

    @MockK
    private lateinit var dataExpander: DataExpander<Int, String>

    private lateinit var dataLoader: ExpandableDataLoader<Int, String>

    @BeforeEach
    fun before() {
        this.dataLoader = ExpandableDataLoader(delegateDataLoader, dataExpander)
    }

    @Test
    fun `should extract the raw data and expand it`() {
        val desiredSize = 3
        val scenario1 = aScenario(arbitraryValue(), aDataPoint(), aDataPoint(), aDataPoint())
        val scenario2 = aScenario(arbitraryValue(), aDataPoint(), aDataPoint())
        val scenario3 = aScenario(arbitraryValue(), aDataPoint())
        val rawData = listOf(scenario1, scenario2)
        val expandedScenarios = listOf(scenario1, scenario2, scenario3)

        every { delegateDataLoader.loadData() } returns rawData
        every { dataExpander.expand(rawData, desiredSize) } returns expandedScenarios

        val result = dataLoader.loadData()
        result shouldEqual expandedScenarios
    }
}