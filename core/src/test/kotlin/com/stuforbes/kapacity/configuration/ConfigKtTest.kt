package com.stuforbes.kapacity.configuration

import com.stuforbes.kapacity.test.shouldEqual
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ConfigKtTest {

    @Nested
    @DisplayName("intConfig")
    inner class IntConfig {
        @Test
        fun `should read int config values correctly`() {
            "property.int".intConfig() shouldEqual 1234
        }
    }

    @Nested
    @DisplayName("longConfig")
    inner class LongConfig {
        @Test
        fun `should read long config values correctly`() {
            "property.long".longConfig() shouldEqual 123456789
        }
    }

    @Nested
    @DisplayName("stringConfig")
    inner class StringConfig {
        @Test
        fun `should read string config values correctly`() {
            "property.string".stringConfig() shouldEqual "a string property"
        }
    }

    @Nested
    @DisplayName("booleanConfig")
    inner class BooleanConfig {
        @Test
        fun `should read boolean config values correctly`() {
            "property.boolean".booleanConfig() shouldEqual true
        }
    }

    @Nested
    @DisplayName("longConfigOrNull")
    inner class LongConfigOrNull {
        @Test
        fun `should return the config value if it exists`() {
            "property.long".longConfigOrNull() shouldEqual 123456789
        }

        @Test
        fun `should return null if it doesn't exist`() {
            "property.unknown.long".longConfigOrNull() shouldEqual null
        }
    }
}