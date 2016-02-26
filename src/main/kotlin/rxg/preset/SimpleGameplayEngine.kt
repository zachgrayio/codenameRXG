package rxg.preset

import rx.lang.kotlin.BehaviourSubject
import rx.subjects.BehaviorSubject
import rxg.engine.IntervalGameplayEngine
import rxg.frame.Frame
import rxg.input.KeyEvent
import java.util.concurrent.TimeUnit

abstract  class SimpleGameplayEngine : IntervalGameplayEngine {
    override val timeUnit: TimeUnit get() = TimeUnit.MILLISECONDS
    override val interval: Long get() = 1
    override val keySubject: BehaviorSubject<KeyEvent> = BehaviourSubject()
    override val framePointer = Frame()
//    override fun update() {
//        // todo: simple gameplay engine per-frame stuff?
//    }
}