package com.stuforbes.kapacity.sample.recorder

import com.stuforbes.kapacity.recorder.prometheus.PrometheusFlightRecorder

class PrometheusFlightRecorderImpl : PrometheusFlightRecorder(
    "kafka_sample_latency",
    15
)