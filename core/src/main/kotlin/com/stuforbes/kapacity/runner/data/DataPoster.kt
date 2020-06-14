package com.stuforbes.kapacity.runner.data

import com.stuforbes.kapacity.model.TimeSeriesData

/**
 * Posts data items to a target
 *
 * @param T the type of data item to be posted
 */
interface DataPoster<T> {

    /**
     * Post the data
     *
     * @param data to be posted
     */
    fun post(data: TimeSeriesData<T>)
}