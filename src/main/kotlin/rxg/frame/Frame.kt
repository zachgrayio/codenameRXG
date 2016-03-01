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

    fun actorCollisions() : MutableList<Pair<Actor, Actor>> {
        val collisions:MutableList<Pair<Actor, Actor>> = mutableListOf()
        if(actors.isEmpty()) return collisions
        actors.forEach { a1 ->
            actors.forEach { a2 ->
                if(a1 !== a2 && is2DCollision(a1, a2)) collisions.add(Pair(a1,a2))
            }
        }
        return collisions
    }

    fun is2DCollision(a1: Actor, a2: Actor): Boolean {
        val collisionX = a1.x + a1.size.x >= a2.x && a2.x + a2.size.x >= a1.x
        val collisionY = a1.y + a1.size.y >= a2.y && a2.y + a2.size.y > a1.y
        return collisionX && collisionY
    }
}
