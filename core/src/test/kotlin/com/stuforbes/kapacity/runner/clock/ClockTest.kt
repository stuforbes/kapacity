package com.stuforbes.kapacity.runner.clock

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Timer
import java.util.TimerTask

@ExtendWith(MockKExtension::class)
internal class ClockTest {

    @RelaxedMockK
    private lateinit var timer: Timer

    @InjectMockKs
    private lateinit var clock: Clock

    @Test
    fun `starting a clock should schedule a new timer task`() {
        val onTick = mockk<(Long) -> Unit>()

        clock.start(onTick)

        verify { timer.schedule(any(), 0L, 1000L) }
    }

    @Test
    fun `should invoke the onTick function when the timer task is invoked`() {
        val onTick = mockk<(Long) -> Unit>(relaxed = true   )
        val timerTaskSlot = slot<TimerTask>()
        every { timer.schedule(capture(timerTaskSlot), any<Long>(), any()) } just Runs

        clock.start(onTick)

        timerTaskSlot.captured.run()

        verify { onTick(withArg {
            assertThat(it).isLessThan(TOLERANCE_MS)
        }) }
    }

    companion object {
        private const val TOLERANCE_MS = 100L
    }
}