package rxg.common

import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import java.util.*
import java.util.concurrent.ThreadLocalRandom

interface Factory<T> {
    fun create() : T
}

infix fun String.isRotation(other: String): Boolean {
    return this.plus(this).contains(other)
}

data class IntervalHandle(private var interval: Observable<Long>, private var closure:(Long)->Unit) {
    var paused:Boolean = false
    var subscription:Subscription? = interval
        .observeOn(Schedulers.immediate())
        .subscribe { if(!paused) closure(it) }
}

fun randomFloatBetween(min:Float, max:Float): Float {
    if(min < 0 || max < 0) throw RuntimeException("Only positive values are supported.")
    return Random().nextFloat().times((min - max).plus(1))
}