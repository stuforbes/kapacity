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