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

import com.stuforbes.kapacity.model.Scenario

/**
 * Increases the size of input scenarios to a desired size.
 *
 * @param scenarioDecorator Used to copy an original source scenario, to provide an expanded data set.
 */
class DataExpander<Id, T>(
    private val scenarioDecorator: ExpandedScenarioDecorator<Id, T>
) {

    /**
     * Expand the source data set to the desired size
     *
     * @param source The initial input data set
     * @param desiredSize The desired size of the resulting data set
     *
     * @return A list of scenarios with a size equal to desiredSize
     */
    fun expand(source: List<Scenario<Id, T>>, desiredSize: Int): List<Scenario<Id, T>> {
        val duplicates = desiredSize / source.size
        val additional = desiredSize % source.size

        return duplicate(source, duplicates) +
                takeAndDecorate(source, additional, duplicates + 1)
    }

    private fun duplicate(source: List<Scenario<Id, T>>, duplicationFactor: Int) =
        if (duplicationFactor > 0) {
            source.flatMap { sourceItem ->
                (1..duplicationFactor).map {
                    scenarioDecorator.decorate(sourceItem, it)
                }
            }
        } else emptyList()

    private fun takeAndDecorate(source: List<Scenario<Id, T>>, takeAmount: Int, duplicateNumber: Int) =
        source.take(takeAmount).map {
            scenarioDecorator.decorate(it, duplicateNumber)
        }
}

/**
 * Single function interface responsible for creating duplicates of scenarios
 *
 * @param Id The type of Scenario Id
 * @param T The type of Data points within the scenario
 */
interface ExpandedScenarioDecorator<Id, T> {

    /**
     * Copies a source scenario, creating a new version of the scenario, specific to the duplicate number
     */
    fun decorate(sourceItem: Scenario<Id, T>, duplicateNumber: Int): Scenario<Id, T>
}