/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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