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

import com.stuforbes.kapacity.configuration.intConfig
import com.stuforbes.kapacity.model.Scenario
import com.stuforbes.kapacity.util.Loggable

/**
 * Delegate wrapper around a {@link DataLoader}. Scenarios are loaded from the delegate loader,
 * and immediately expanded upon.
 *
 * The desired number of scenarios is configured in the config parameter:
 * 'scenarios.number'.
 * Input scenarios will be expanded to this amount after loading
 *
 * @param dataLoader The delegate loader
 * @param dataExpander Increases the number of loaded scenarios
 */
class ExpandableDataLoader<Id, T>(
    private val dataLoader: DataLoader<Id, T>,
    private val dataExpander: DataExpander<Id, T>
): DataLoader<Id, T>, Loggable {

    private val numScenarios = "scenarios.number".intConfig()

    override fun loadData(): List<Scenario<Id, T>> {
        debug { "Loading data" }
        val scenarios = dataLoader.loadData()

        debug { "Expanding data set size from ${scenarios.size} to $numScenarios" }
        return dataExpander.expand(scenarios, numScenarios)
    }
}