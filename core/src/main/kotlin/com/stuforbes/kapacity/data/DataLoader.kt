package com.stuforbes.kapacity.data

import com.stuforbes.kapacity.model.Scenario

/**
 * Loads a list of scenarios from a source
 *
 * @param Id The type of Scenario Id
 * @param T The type of Data points within the scenario
 */
interface DataLoader<Id, T> {

    /**
     * Load the data from a source
     *
     * @return a list of loaded scenarios
     */
    fun loadData(): List<Scenario<Id, T>>
}