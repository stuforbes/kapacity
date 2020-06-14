@file:Suppress("UNCHECKED_CAST")

package com.stuforbes.kapacity.data

import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.test.shouldEqual
import com.stuforbes.kapacity.test.someScenarios
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class DataExpanderTest {

    @MockK
    private lateinit var scenarioDecorator: ExpandedScenarioDecorator<Int, String>

    private lateinit var dataExpander: DataExpander<Int, String>

    private lateinit var scenarios: List<Scenario<Int, String>>

    @BeforeEach
    fun before() {
        this.dataExpander = DataExpander(scenarioDecorator)

        this.scenarios = someScenarios(3)

        every { scenarioDecorator.decorate(any(), any()) } answers {
            it.invocation.args[0] as Scenario<Int, String>
        }
    }

    @Test
    fun `should duplicate source nodes if the desired number is a multiple of the source size`() {
        runExpanderAndCheckSizeIs(6)

        verify {
            scenarioDecorator.decorate(scenarios[0], 1)
            scenarioDecorator.decorate(scenarios[1], 1)
            scenarioDecorator.decorate(scenarios[2], 1)
            scenarioDecorator.decorate(scenarios[0], 2)
            scenarioDecorator.decorate(scenarios[1], 2)
            scenarioDecorator.decorate(scenarios[2], 2)
        }
    }

    @Test
    fun `should duplicate source nodes and add additional decorated if the desired number is not a multiple of the source size`() {
        runExpanderAndCheckSizeIs(5)

        verify {
            scenarioDecorator.decorate(scenarios[0], 1)
            scenarioDecorator.decorate(scenarios[1], 1)
            scenarioDecorator.decorate(scenarios[2], 1)
            scenarioDecorator.decorate(scenarios[0], 2)
            scenarioDecorator.decorate(scenarios[1], 2)
        }
    }

    @Test
    fun `should not duplicate any source nodes but add additional if the desired number is less than the source size`() {
        runExpanderAndCheckSizeIs(2)

        verify {
            scenarioDecorator.decorate(scenarios[0], 1)
            scenarioDecorator.decorate(scenarios[1], 1)
        }
    }

    @Test
    fun `should return an empty list if the desired number is 0`() {
        runExpanderAndCheckSizeIs(0)

        verify(exactly = 0) {
            scenarioDecorator.decorate(any(), any())
        }
    }

    private fun runExpanderAndCheckSizeIs(desiredSize: Int) {
        val expanded = dataExpander.expand(scenarios, desiredSize)
        expanded.size shouldEqual desiredSize
    }
}