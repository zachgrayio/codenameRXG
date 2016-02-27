package rxg.frame

import rxg.frame.actor.Actor
import rxg.frame.actor.ActorImpl
import rxg.logger.consoleLogger


class Frame() {
    private var logger = consoleLogger(javaClass)
    var actors:MutableList<Actor> = kotlin.collections.arrayListOf()
    var delta:Long = 1

    fun setDelta(deltaMs: Long): Frame {
        delta = deltaMs
        return this
    }
}
