/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.recorder.prometheus

import com.stuforbes.kapacity.configuration.booleanConfig
import com.stuforbes.kapacity.configuration.intConfig
import com.stuforbes.kapacity.configuration.stringConfig

/**
 * Configuration object for Prometheus access
 */
data class Prometheus(
    val latencyMetric: String,
    val resolutionSeconds: Int,
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
            latencyMetric = "prometheus.latency.metric".stringConfig(),
            resolutionSeconds = "prometheus.resolution.seconds".intConfig(),
            protocol = if ("prometheus.secure".booleanConfig()) "https"
            else "http",
            host = "prometheus.host".stringConfig(),
            port = "prometheus.port".intConfig()
        )
    }
}