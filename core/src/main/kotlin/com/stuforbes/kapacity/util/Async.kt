package com.stuforbes.kapacity.util

/**
 * Wait until the condition has been met, up to the max wait time
 *
 * @param maxWait Maximum amount of time to wait, in ms
 * @param condition Predicate to check
 */
suspend fun waitUntil(maxWait: Long, condition: () -> Boolean) {
    val start = System.currentTimeMillis()

    while (condition().not()) {
        if (System.currentTimeMillis() - start > maxWait) {
            throw IllegalStateException("Operation did not complete within $maxWait ms")
        }
    }
}