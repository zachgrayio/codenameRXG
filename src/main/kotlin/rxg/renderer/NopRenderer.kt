package rxg.renderer

import rx.Observable
import rxg.frame.Frame
import rxg.input.KeyEvent
import rxg.logger.consoleLogger
import rxg.resource.ResourceManager
import java.util.concurrent.TimeUnit

class NopRenderer(override val width: Int, override val height: Int) : IOEngine {
    override val resourceManager:ResourceManager get() = throw NotImplementedError()

    private var logger = consoleLogger(javaClass)
    private var renderedFrames:Long = 0

    init {
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribe {
                logger.log("renderedFrames","%d".format(renderedFrames))
            }
    }

    override fun close() {

    }

    override fun keyOutput(): Observable<KeyEvent> {
        return Observable.never()
    }

    override fun render(frame: Frame) {
        renderedFrames++
    }
}