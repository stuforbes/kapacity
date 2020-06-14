@file:Suppress("unused")

package com.stuforbes.kapacity.recorder

/**
 * Implementation of {@link FlightRecorder} that does nothing. This is to be used when
 * test recording is to be done externally to Kapacity
 */
class NoOpFlightRecorder : FlightRecorder<Unit>{

    override fun start() {
        // No Op
    }

    override fun stop() {
        // No Ops
    }
}