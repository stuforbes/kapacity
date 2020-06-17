/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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