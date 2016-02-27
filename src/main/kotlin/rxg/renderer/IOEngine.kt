package rxg.renderer

import rx.Observable
import rxg.frame.Frame
import rxg.input.KeyEvent
import rxg.resource.ResourceManager

interface IOEngine : AutoCloseable {
    val width:Int
    val height:Int
    val resourceManager:ResourceManager
    val scale:Float

    fun render(frame: Frame)
    fun keyOutput(): Observable<KeyEvent>
    fun pollInput()

//    var lastRenderMs:Long
//    fun delta():Int {
//        val timeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime())
//        val rendererDelta = if(lastRenderMs == 0.toLong()) 1.toLong() else timeMs - lastRenderMs
//        lastRenderMs = timeMs
//        return rendererDelta.toInt()
//    }
}