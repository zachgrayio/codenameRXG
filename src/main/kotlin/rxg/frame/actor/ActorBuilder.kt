package rxg.frame.actor

import rx.subjects.BehaviorSubject
import java.util.*

class ActorBuilder : Actor {
    override val positionSubject: BehaviorSubject<Position>
        get() = throw UnsupportedOperationException()

    override var tag: String? = null
    override var previousAnimationKey: String?
        get() = throw UnsupportedOperationException()
        set(value) { }

    override var defaultAnimationKey: String? = null
    override var position: Position = Position(x = 0f, y = 0f)
    override var rotation:Float = 0f
    override var size: Size = Size()
    override var speedX:Float = 1f
    override var speedY:Float = 1f
    override var autoReverseEnabled = true
    override var frameIntervalMs:Long = 100
    override var animations: HashMap<String, List<String>> = HashMap()
    var sprites:List<String> = listOf()
    override var currentAnimationKey:String? = null
    override var reverseSprite: Boolean = false
    override var spawned:Boolean = false

    fun animation(key:String, default:Boolean = false, closure:()->List<String>) {
        animations.put(key, closure())
        if(default) defaultAnimationKey = key
    }
    fun build(): Actor {
        val a = ActorImpl(rotation, size, speedX, speedY, autoReverseEnabled, frameIntervalMs, animations, defaultAnimationKey, spawned, tag)
        a.position = position
        return a
    }
    override fun currentSprite(): String {
        throw UnsupportedOperationException()
    }
}