package rxg.frame.actor

import rxg.frame.Size
import java.util.*

class ActorBuilder : Actor {
    override var x: Float = 0f
    override var y: Float = 0f
    override var rotation:Float = 0f
    override var size: Size = Size()
    override var speed:Float = 0f
    override var autoReverseEnabled = true
    override var frameIntervalMs:Long = 100
    override var animations: HashMap<String, List<String>> = HashMap()
    var sprites:List<String> = listOf()
    override var currentAnimationKey:String? = null
    override var reverseSprite: Boolean = false
    fun animation(name:String, default:Boolean = false, closure:()->List<String>) {
        animations.put(name, closure())
        if(default) currentAnimationKey = name
    }
    fun build(): Actor {
        return ActorImpl(rotation, size, speed, autoReverseEnabled, frameIntervalMs, animations, currentAnimationKey)
    }
    override fun currentSprite(): String {
        throw UnsupportedOperationException()
    }
}