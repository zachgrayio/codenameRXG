package rxg.frame.actor

import java.util.*

class ActorBuilder {
    var x:Float = 0f
    var y:Float = 0f
    var rotation:Float = 0f
    var size:Actor.Size = Actor.Size()
    var speed:Float = 0f
    var frameIntervalMs:Long = 100
    var sprites:List<String> = listOf()
    var currentAnimationKey:String? = null
    fun animation(name:String, default:Boolean = false, closure:()->List<String>) {
        animations.put(name, closure())
        if(default) currentAnimationKey = name
    }
    var animations: HashMap<String, List<String>> = HashMap()
    fun build(): Actor {
        return Actor(x, y, rotation, size, speed, frameIntervalMs, animations, currentAnimationKey)
    }
}