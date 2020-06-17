/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Provide logging capability to classes extending this interface
 */
interface Loggable {

    private val logger: Logger
        get() = getOrCreateFor(this.javaClass)

    fun trace(msg: () -> String){
        if(logger.isTraceEnabled) logger.trace(msg())
    }

    fun debug(msg: () -> String) {
        if (logger.isDebugEnabled) logger.debug(msg())
    }
    fun debug(msg: String) {
        if (logger.isDebugEnabled) logger.debug(msg)
    }

    fun info(msg: () -> String) {
        if (logger.isInfoEnabled) logger.info(msg())
    }

    fun warn(msg: () -> String, exception: Throwable? = null) {
        if (logger.isWarnEnabled) logger.warn(msg(), exception)
    }

    fun error(msg: () -> String, exception: Throwable? = null) {
        if (logger.isErrorEnabled) logger.error(msg(), exception)
    }
}

private val loggerMap = mutableMapOf<String, Logger>()

private fun <T : Loggable> getOrCreateFor(clazz: Class<T>): Logger {
    if(loggerMap.containsKey(clazz.name).not()) {
        loggerMap[clazz.name] = LoggerFactory.getLogger(clazz)
    }
    return loggerMap[clazz.name]!!
}