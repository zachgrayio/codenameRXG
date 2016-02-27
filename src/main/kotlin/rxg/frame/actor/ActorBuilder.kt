package rxg.frame.actor

import rxg.frame.Size
import java.util.*

class ActorBuilder {
    var rotation:Float = 0f
    var size: Size = Size()
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
        return Actor(rotation, size, speed, frameIntervalMs, animations, currentAnimationKey)
    }
}