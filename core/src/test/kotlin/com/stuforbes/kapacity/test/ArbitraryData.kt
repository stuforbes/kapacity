package com.stuforbes.kapacity.test

import java.util.concurrent.atomic.AtomicInteger

inline fun <reified T> arbitraryValue(): T = when(T::class) {
    String::class -> nextString() as T
    Int::class -> nextInt() as T
    Long::class -> nextLong() as T
    else -> throw IllegalStateException("Cannot create arbitrary value for type ${T::class.qualifiedName}")
}

private val counter = AtomicInteger(1)

fun nextString() = "str-${counter.getAndIncrement()}"

fun nextInt() = counter.getAndIncrement()

fun nextLong() = counter.getAndIncrement().toLong()