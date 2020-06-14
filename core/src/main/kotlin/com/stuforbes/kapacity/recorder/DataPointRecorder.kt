package com.stuforbes.kapacity.recorder

/**
 * Implementations of this interface can be used to record time aware data points. When calling the recordDataPoint() function,
 * a callback is returned. Invoking this at some point in the future means the duration between first recording the data point
 * and invoking the callback can be measured.
 */
interface DataPointRecorder<Data> {

    /**
     * Begin recording a time-measured operation. A callback will be returned immediately, which can be invoked upon completion of the operation
     *
     * @return OnCompletion handler, used to indicate that an operation has completed
     */
    fun recordDataPoint(): OnCompletion<Data>
}

typealias OnCompletion<Data> = (Data)->Unit

/**
 * Composite interface used to combine {@link FlightRecorder} and {@link DataPointRecorder}
 */
interface DataPointRecordingFlightRecorder<Data, Result> :
    FlightRecorder<Result>, DataPointRecorder<Data>

/**
 * Implementations of this interface can be passed 
 */
interface DataPointRecordable<Data> {
    fun registerDataPointRecorder(dataPointRecorder: DataPointRecorder<Data>)
}