package com.stuforbes.kapacity.test

import org.assertj.core.api.Assertions.assertThat

infix fun <T> T?.shouldEqual(other: T?) =
    assertThat(this).isEqualTo(other)

infix fun <K, V> Map<K, V>.shouldContainAllOf(other: Map<K, V>) =
    assertThat(this).containsAllEntriesOf(other)

infix fun String.shouldContain(expectedStr: String) =
    assertThat(this).contains(expectedStr)

inline fun <reified T> Any.shouldBeA() =
    assertThat(this).isExactlyInstanceOf(T::class.java)