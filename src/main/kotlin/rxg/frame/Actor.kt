package rxg.frame

import rx.Observable
import rx.subjects.BehaviorSubject
import rxg.engine.GameplayEngine
import rxg.resource.ResourceManager
import java.awt.image.BufferedImage
import java.util.concurrent.TimeUnit

class Actor(
    var x:Float = 0f,
    var y:Float = 0f,
    var speed:Float = 0f,
    var frameIntervalMs:Long = 100,
    var sprites: List<String> = listOf()) {

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