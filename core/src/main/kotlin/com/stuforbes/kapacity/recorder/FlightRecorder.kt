package com.stuforbes.kapacity.recorder

/**
 * Record data relevant to the test run
 *
 * @param Result the data returned from the recorder on completion
 */
interface FlightRecorder<Result> {

    /**
     * Begin recording operation
     */
    fun start()

    /**
     * Stop recording, and return result
     *
     * @return The recording result
     */
    fun stop(): Result
}