package rxg.frame.actor

import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

open class Actor(
    var x:Float = 0f,
    var y:Float = 0f,
    val size:Size = Actor.Size(),
    val speed:Float = 0f,
    val frameIntervalMs:Long = 100,
    val sprites: List<String> = listOf()) {

    data class Size(var x:Float = 0f, var y:Float = 0f)

    private val frameSpriteSubject = BehaviorSubject.create<String>()
    private val singleSprite:String? = if(sprites.count() == 1) sprites[0] else null

    init {
        if(singleSprite == null && !sprites.isEmpty()) {
            var frameIndex = 0
            Observable.interval(frameIntervalMs, TimeUnit.MILLISECONDS)
                .map {
                    if(frameIndex + 1 >= sprites.count()) frameIndex = -1
                    sprites[++frameIndex]
                }
                .startWith(sprites[0])
                .subscribe(frameSpriteSubject)
        }
    }

    fun currentSprite():String {
        return singleSprite ?: frameSpriteSubject.value ?: frameSpriteSubject.toBlocking().first()
    }
}