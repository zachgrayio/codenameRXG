package rxg.gameplay.engine

import rx.Observable
import rxg.frame.Frame
import rxg.gameplay.engine.GameplayEngine
import java.util.concurrent.TimeUnit

/**
 * Calls update() on the specified interval, then emits the current value of framePointer
 */
interface IntervalGameplayEngine : GameplayEngine {
    val interval:Long
    val timeUnit: TimeUnit
    fun onInterval()
    override fun frameOutput(): Observable<Frame> {
        return Observable.interval(interval, timeUnit)
            .doOnNext { onInterval() }
            .map { framePointer }
    }
}