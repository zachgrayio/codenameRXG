package rxg.gameplay.dsl

import rx.Observable
import rxg.common.IntervalHandle
import java.util.concurrent.TimeUnit

/**
 * Most of the gameplay DSL is defined in GameplayEngine interface out of necessity, but much is here.
 */

fun doEvery(interval:Long, timeUnit: TimeUnit, closure:(Long)->Unit): IntervalHandle {
    return IntervalHandle(Observable.interval(interval, timeUnit), closure)
}

fun ifNot(notValue:Boolean, closure:()->Unit) : ()->Unit {
    return if(!notValue) closure else { {} }
}

fun ifIs(isValue:Boolean, closure:()->Unit) : ()->Unit {
    return if(isValue) closure else { {} }
}