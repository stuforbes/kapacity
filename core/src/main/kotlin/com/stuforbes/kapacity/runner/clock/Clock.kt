/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.runner.clock

import com.stuforbes.kapacity.util.Loggable
import com.stuforbes.kapacity.util.now
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicReference

/**
 * Can be used to invoke a function every second.
 *
 * When started, the supplied onTick callback will be invoked every second, until the stop function is called.
 * A clock cannot be started multiple times. Once started, calling start again before stop has been called will
 * result in an exception being thrown
 *
 * @param timer Used to schedule a job to run every 1 second
 */
class Clock(
    private val timer: Timer = Timer()
) : Loggable {

    private val startTime: AtomicReference<Long?> = AtomicReference(null)

    /**
     * Start the clock.
     *
     * @param onTick will be invoked every second, passing in the total timer duration
     * @throws IllegalStateException if the clock is already running
     */
    fun start(onTick: (Long) -> Unit) {
        validateNotRunning {
            debug { "Starting clock" }
            startTimer(onTick)
        }
    }

    /**
     * Stop the clock
     *
     * @throws IllegalStateException if the clock is not running
     */
    fun stop() {
        validateRunning {
            timer.cancel()
            timer.purge()
            startTime.set(null)
        }
    }

    private fun validateRunning(ifRunning: () -> Unit) {
        check(startTime.get() != null) { "Clock has not been started" }
        ifRunning()
    }

    private fun validateNotRunning(ifNotRunning: () -> Unit) {
        check(startTime.get() == null) { "Clock has already been started" }
        ifNotRunning()
    }

    private fun startTimer(onTick: (Long) -> Unit){
        startTime.set(now())
        timer.schedule(object : TimerTask() {
            override fun run() {
                onTick(now() - startTime.get()!!)
            }
        }, 0, 1000)
    }
}