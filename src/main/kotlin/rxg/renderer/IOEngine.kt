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
}