package rxg.frame.actor

import rx.subjects.BehaviorSubject
import java.util.*

interface Actor {
    var position:Position
    val positionSubject:BehaviorSubject<Position>
    var rotation:Float
    var size: Size
    var speedX:Float
    var speedY:Float
    val autoReverseEnabled:Boolean
    val frameIntervalMs:Long
    val animations: HashMap<String, List<String>>
    var currentAnimationKey:String?
    var defaultAnimationKey:String?
    var previousAnimationKey:String?
    var reverseSprite: Boolean
    var spawned: Boolean
    var tag:String?
    fun currentSprite():String

    fun isColliding(a2: Actor?): Boolean {
        if(a2 == null) return false
        if(this === a2) return false
        if(!(spawned && a2.spawned)) return false
        val collisionX = this.position.x + this.size.x >= a2.position.x && a2.position.x + a2.size.x >= this.position.x
        val collisionY = this.position.y - this.size.y <= a2.position.y && a2.position.y - a2.size.y <= this.position.y
        return collisionX && collisionY
    }

    fun playPrevious(): Actor {
        currentAnimationKey = previousAnimationKey ?: defaultAnimationKey ?: animations.keys.first()
        return this
    }

    fun playDefault(): Actor {
        if(previousAnimationKey != null && previousAnimationKey != defaultAnimationKey)
            previousAnimationKey = currentAnimationKey

        currentAnimationKey = defaultAnimationKey ?: animations.keys.first()
        return this
    }

    infix fun play(key:String): Actor {
        if(previousAnimationKey != null && previousAnimationKey != key) {
            previousAnimationKey = currentAnimationKey
        }
        currentAnimationKey = key
        return this
    }

    infix fun latchTo(other:Actor): Actor {
        latchTo(other, null)
        return this
    }

    fun latchTo(other:Actor, whileTrue:(()->Boolean)?): Actor {
        val xoff = position.x - other.position.x
        val yoff = position.y - other.position.y
        other.positionSubject
            .takeWhile { if(whileTrue == null) true else whileTrue() }
            .map { Position(it.x + xoff, it.y + yoff) }
            .subscribe { p -> position = p }
        return this
    }
}