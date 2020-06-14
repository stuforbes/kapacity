package com.stuforbes.kapacity.test

import org.assertj.core.api.Assertions
import kotlin.reflect.jvm.isAccessible

fun <T> Any.shouldHaveAFieldValueOf(fieldName: String, expectedValue: T){
    val actualValue = fieldValueNamed<T>(fieldName)
    Assertions.assertThat(actualValue).isEqualTo(expectedValue)
}

fun <T> Any.fieldValueNamed(fieldName: String): T {
    val field = this::class.members.find { it.name == fieldName }
    Assertions.assertThat(field).isNotNull

    field?.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field!!.call(this) as T
}