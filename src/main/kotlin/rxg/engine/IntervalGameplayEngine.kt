package rxg.engine

import rx.Observable
import rxg.frame.Frame
import java.util.concurrent.TimeUnit

/**
 * Calls update() on the specified interval, then emits the current value of framePointer
 */
interface IntervalGameplayEngine : GameplayEngine {
    val interval:Long
    val timeUnit: TimeUnit
    fun update()
    override fun frameOutput(): Observable<Frame> {
        return Observable.interval(interval, timeUnit)
            .doOnNext { update() }
            .map { framePointer }
    }
}