package rxg.frame.actor

import rx.Observable
import rx.subjects.BehaviorSubject
import rxg.frame.Size
import java.util.*
import java.util.concurrent.TimeUnit

open class ActorImpl(
    override var rotation:Float = 0f,
    override val size:Size = Size(),
    override val speed:Float = 0f,
    override val autoReverseEnabled:Boolean = true,
    override val frameIntervalMs:Long = 100,
    override val animations: HashMap<String, List<String>> = HashMap(),
    override var currentAnimationKey:String?) : Actor {

    override var reverseSprite: Boolean = false

    private var _x:Float = 0f
    override var x:Float
        get() = _x
        set(value) {
            if(autoReverseEnabled) reverseSprite = value < _x
            _x = value
        }
    override var y:Float = 0f

    private val frameSpriteSubject = BehaviorSubject.create<String>()
    private var singleSprite:String? = null

    init {
        if(currentAnimationKey == null) currentAnimationKey = animations.keys.first()
        initializeAnimation()
    }

    private fun initializeAnimation() {
        if(animations.isEmpty()) return
        if(animations.count() == 1) {
            val singleAnimation = animations.values.firstOrNull()
            if(singleAnimation?.count() == 1) {
                singleSprite = singleAnimation?.first()
                return
            }
        }
        var frameIndex = -1
        Observable.interval(frameIntervalMs, TimeUnit.MILLISECONDS)
            .map {
                val sprites = animations[currentAnimationKey]
                if(frameIndex + 1 >= sprites!!.count()) frameIndex = -1
                sprites[++frameIndex]
            }
            .subscribe(frameSpriteSubject)
    }

    override fun currentSprite():String {
        return singleSprite ?: frameSpriteSubject.value ?: frameSpriteSubject.toBlocking().first()
    }
}