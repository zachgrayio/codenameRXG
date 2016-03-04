package rxg.frame.actor

import java.util.*

interface Actor {
    var x:Float
    var y:Float
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
    fun currentSprite():String

    fun isColliding(a2: Actor?): Boolean {
        if(a2 == null) return false
        if(this === a2) return false
        if(!(spawned && a2.spawned)) return false
        val collisionX = this.x + this.size.x >= a2.x && a2.x + a2.size.x >= this.x
        val collisionY = this.y - this.size.y <= a2.y && a2.y - a2.size.y <= this.y
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
}