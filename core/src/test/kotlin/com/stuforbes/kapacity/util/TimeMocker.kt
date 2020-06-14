package com.stuforbes.kapacity.util

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

private const val timeClassName = "com.stuforbes.kapacity.util.TimeKt"

fun primeNowWith(times: List<Long>, testFn: ()->Unit) {
    mockkStatic(timeClassName)
    every { now() } returnsMany times

    testFn()

    unmockkStatic(timeClassName)
}