package com.stuforbes.kapacity.recorder.prometheus

import com.stuforbes.kapacity.configuration.booleanConfig
import com.stuforbes.kapacity.configuration.intConfig
import com.stuforbes.kapacity.configuration.stringConfig

/**
 * Configuration object for Prometheus access
 */
data class Prometheus(
    val protocol: String,
    val host: String,
    val port: Int,
    val endpoint: String = "/api/v1/query_range"
) {
    companion object {
        /**
         * Create a new Prometheus config object from the configuration file
         */
        fun fromConfig() = Prometheus(
            protocol = if ("prometheus.secure".booleanConfig()) "https"
            else "http",
            host = "prometheus.host".stringConfig(),
            port = "prometheus.port".intConfig()
        )
    }
}