package com.stuforbes.kapacity.sample.async

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import kotlin.concurrent.thread

class JobRunner{
    var job: ClosableRunnable? = null
}

object BackgroundOperation : ApplicationFeature<Application, JobRunner, Unit> {
    override val key = AttributeKey<Unit>("BackgroundOperation")

    override fun install(pipeline: Application, configure: JobRunner.() -> Unit) {
        val runner = JobRunner().apply(configure)
        runner.job?.run {
            thread { run() }
        }
    }
}