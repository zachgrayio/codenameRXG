package rxg.frame.actor

import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit

open class Actor(
    var x:Float = 0f,
    var y:Float = 0f,
    var rotation:Float = 0f,
    val size:Size = Actor.Size(),
    val speed:Float = 0f,
    val frameIntervalMs:Long = 100,
    val animations: HashMap<String, List<String>> = HashMap(),
    var currentAnimationKey:String?) {

    data class Size(var x:Float = 0f, var y:Float = 0f)

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
        var sprites:List<String>?
        var frameIndex = -1
        Observable.interval(frameIntervalMs, TimeUnit.MILLISECONDS)
            .map {
                sprites = animations[currentAnimationKey]
                if(frameIndex + 1 >= sprites!!.count()) frameIndex = -1
                sprites!![++frameIndex]
            }
            .subscribe(frameSpriteSubject)
    }

    fun currentSprite():String {
        return singleSprite ?: frameSpriteSubject.value ?: frameSpriteSubject.toBlocking().first()
    }
}