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

import com.natpryce.konfig.ConfigurationProperties.Companion.fromOptionalFile
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EmptyConfiguration
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.Misconfiguration
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.intType
import com.natpryce.konfig.longType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import com.stuforbes.kapacity.engine.Engine
import java.io.File

/**
 * Uses the string value as a config key, and returns the int value it represents
 * @return The int config value the key points to
 * @throws Misconfiguration if the config value doesn't exist
 */
fun String.intConfig() = config[Key(this, intType)]

/**
 * Uses the string value as a config key, and returns the long value it represents
 * @return The long config value the key points to
 * @throws Misconfiguration if the config value doesn't exist
 */
fun String.longConfig() = config[Key(this, longType)]

/**
 * Uses the string value as a config key, and returns the string value it represents
 * @return The string config value the key points to
 * @throws Misconfiguration if the config value doesn't exist
 */
fun String.stringConfig() = config[Key(this, stringType)]

/**
 * Uses the string value as a config key, and returns the boolean value it represents
 * @return The boolean config value the key points to
 * @throws Misconfiguration if the config value doesn't exist
 */
fun String.booleanConfig() = config[Key(this, booleanType)]

/**
 * Uses the string value as a config key, and if it exists, returns the long value it represents, otherwise returns null
 * @return The long config value the key points to, or null
 */
fun String.longConfigOrNull() = orNull { longConfig() }

private const val PROPERTY_FILE_NAME = "test.properties"
private val optionalConfigFile = Engine::class.java.getResource("/$PROPERTY_FILE_NAME")
private val baseConfiguration = if (optionalConfigFile != null) {
    fromOptionalFile(File(optionalConfigFile.file))
} else EmptyConfiguration

private val config = systemProperties()
    .overriding(EnvironmentVariables())
    .overriding(baseConfiguration)

private fun <T> orNull(op: () -> T) = try {
    op()
} catch (ex: Misconfiguration) {
    null
}
