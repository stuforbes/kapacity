/*
 * Copyright (c) 2020 com.stuforbes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.stuforbes.kapacity.model

import com.stuforbes.kapacity.runner.TimeSeriesDataProgression
import com.stuforbes.kapacity.runner.asProgression
import com.stuforbes.kapacity.test.aDataPoint
import com.stuforbes.kapacity.test.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TimeSeriesDataProgressionTest {

    private val item11 = aDataPoint(1, "11")
    private val item12 = aDataPoint(2, "12")
    private val item13 = aDataPoint(3, "13")
    private val item14 = aDataPoint(4, "14")
    private val item21 = aDataPoint(1, "21")
    private val item22 = aDataPoint(2, "22")
    private val item23 = aDataPoint(3, "23")
    private val item31 = aDataPoint(1, "31")
    private val item32 = aDataPoint(2, "32")
    private val item41 = aDataPoint(1, "41")

    private lateinit var progression: TimeSeriesDataProgression<String>

    @BeforeEach
    fun before() {
        this.progression = listOf(
            item11, item12, item13, item14,
            item21, item22, item23,
            item31, item32,
            item41
        ).asProgression()
    }

    @Nested
    inner class FirstProgression {
        @Test
        fun `should return an empty list if the first item is after the current time`() {
           progression.allMessagesUpTo(0) shouldEqual emptyList()
        }

        @Test
        fun `should return the first item only if that item is the only one before the current time`() {
            progression.allMessagesUpTo(1) shouldEqual listOf(
                item11, item21, item31, item41
            )
        }

        @Test
        fun `should return the first 3 items if they are all before the current time`() {
            progression.allMessagesUpTo(3) shouldEqual listOf(
                item11, item21, item31, item41,
                item12, item22, item32,
                item13, item23
            )
        }

        @Test
        fun `should return all items if they are all before the current time`() {
            progression.allMessagesUpTo(5) shouldEqual listOf(
                item11, item21, item31, item41,
                item12, item22, item32,
                item13, item23,
                item14
            )
        }
    }

    @Nested
    inner class MidProgression {

        @BeforeEach
        fun before() {
            progression.allMessagesUpTo(1)
        }

        @Test
        fun `should return an empty list if the next progression occurs after the current time`() {
           progression.allMessagesUpTo(1) shouldEqual emptyList()
        }

        @Test
        fun `should return the next item only if that item is the only one before the current time`() {
            progression.allMessagesUpTo(2) shouldEqual listOf(
                item12, item22, item32
            )
        }

        @Test
        fun `should return the next 2 items if they are all before the current time`() {
            progression.allMessagesUpTo(3) shouldEqual listOf(
                item12, item22, item32,
                item13, item23
            )
        }

        @Test
        fun `should return all remaining items if they are all before the current time`() {
            progression.allMessagesUpTo(4) shouldEqual listOf(
                item12, item22, item32,
                item13, item23,
                item14
            )
        }
    }

    @Nested
    inner class FinalProgression {
        @BeforeEach
        fun before() {
            progression.allMessagesUpTo(3)
        }

        @Test
        fun `should return an empty list if the final item occurs after the current time`() {
           progression.allMessagesUpTo(3) shouldEqual emptyList()
        }

        @Test
        fun `should return the final item if it occurs before the current time`() {
            progression.allMessagesUpTo(6) shouldEqual listOf(
                item14
            )
        }
    }

    @Nested
    inner class HasFinished {
        @Test
        fun `returns false if the progression hasn't started yet`() {
            progression.hasFinished() shouldEqual false
        }

        @Test
        fun `returns false if the progression has started but has not yet finished`() {
            progression.allMessagesUpTo(3)
            progression.hasFinished() shouldEqual false
        }

        @Test
        fun `returns true if the progression has finished`() {
            progression.allMessagesUpTo(4)
            progression.hasFinished() shouldEqual true
        }

        @Test
        fun `returns true if the progression has moved beyond the last position`() {
            progression.allMessagesUpTo(7)
            progression.hasFinished() shouldEqual true
        }
    }
}