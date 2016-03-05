package rxg.frame.actor

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit

open class ActorImpl(
    override var rotation:Float = 0f,
    override var size:Size = Size(),
    override var speedX:Float = 1f,
    override var speedY:Float = 1f,
    override val autoReverseEnabled:Boolean = true,
    override val frameIntervalMs:Long = 100,
    override val animations: HashMap<String, List<String>> = HashMap(),
    override var defaultAnimationKey: String? = null,
    override var spawned: Boolean = false,
    override var tag: String? = null)
: Actor {

    override var previousAnimationKey: String? = null
    override var currentAnimationKey:String? = null
    override var reverseSprite: Boolean = false

    override val positionSubject: BehaviorSubject<Position> = BehaviourSubject(Position(x = 0f, y = 0f))
    override var position: Position
        get() = positionSubject.value
        set(value) {
            if(value.x == positionSubject.value.x && value.y == positionSubject.value.y) return
            if(autoReverseEnabled && value.y == positionSubject.value.y) reverseSprite = value.x < positionSubject.value.x
            positionSubject.onNext(value)
        }

    private val frameSpriteSubject = BehaviorSubject.create<String>()
    private var singleSprite:String? = null

    init {
        if(currentAnimationKey == null) currentAnimationKey = defaultAnimationKey ?: animations.keys.first()
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
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(frameSpriteSubject)
    }

    override fun currentSprite():String {
        return singleSprite ?: frameSpriteSubject.value ?: animations[currentAnimationKey]!![0]
    }
}