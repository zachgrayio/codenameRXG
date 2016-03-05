package rxg.common

import rx.Observable
import rx.Subscription
import java.util.*

interface Factory<T> {
    fun create() : T
}

infix fun String.isRotation(other: String): Boolean {
    return this.plus(this).contains(other)
}

data class IntervalHandle(private var interval: Observable<Long>, private var closure:(Long)->Unit) {
    var paused:Boolean = false
    var subscription:Subscription? = interval
        //.observeOn(Schedulers.computation())
        //.onBackpressureBuffer()
        .subscribe({ if(!paused) closure(it) }, { it.printStackTrace() })
}

fun randomFloatInRange(range:ClosedRange<Float>): Float {
    return Random().nextFloat().times((range.endInclusive - range.start).plus(range.start))
}

fun randomLongInRange(range:ClosedRange<Long>): Long {
    return Random().nextLong().times((range.endInclusive - range.start).plus(range.start))
}