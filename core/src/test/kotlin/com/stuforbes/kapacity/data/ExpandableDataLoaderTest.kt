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