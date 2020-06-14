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