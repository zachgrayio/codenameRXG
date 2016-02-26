package demo

import rx.lang.kotlin.BehaviourSubject
import rx.subjects.BehaviorSubject
import rxg.frame.Frame
import rxg.engine.IntervalGameplayEngine
import rxg.frame.Actor
import rxg.input.KeyActions.*
import rxg.input.KeyEvent
import rxg.input.Keys.*
import java.util.concurrent.TimeUnit

/**
 * An interval-based gameplay engine which generates 1 frame every millisecond
 * (for an internal resolution of 1000 frames per second)
 */
class DemoGameplayEngine() : IntervalGameplayEngine {
    override val timeUnit: TimeUnit get() = TimeUnit.MILLISECONDS
    override val interval: Long get() = 1
    override val keySubject: BehaviorSubject<KeyEvent> = BehaviourSubject()
    override val framePointer = Frame()
    init {
        val squad = actor {
            x = 50f
            y = 50f
            speed = 1f
            frameIntervalMs = 100
            sprites = listOf("squad.png")
        }
        // define some gameplay settings and states
        val step = 1f
        var paused = false
        // define some controls
        ESC When RELEASED does { paused = !paused }
        W When PRESSED or HELD does { if(!paused) squad moveUp      step }
        A When PRESSED or HELD does { if(!paused) squad moveLeft    step }
        S When PRESSED or HELD does { if(!paused) squad moveDown    step }
        D When PRESSED or HELD does { if(!paused) squad moveRight   step }
    }
    override fun update() {

    }
}