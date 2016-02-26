package rxg.frame

import rx.Observable
import rx.subjects.BehaviorSubject
import rxg.engine.GameplayEngine

class FrameBus {

    private val frameSubject = BehaviorSubject.create<Frame>()
    private val frameMiddlewares:MutableList<(Frame)->Frame> = mutableListOf()

    fun frameOutput(): Observable<Frame> {
        return frameSubject.asObservable()
            .map { frameMiddlewares.fold(it) { frame, middleware -> middleware(frame) } }
            // set output delta
            .timeInterval()
            .map { it.value.setDelta(it.intervalInMilliseconds) }
    }

    fun bindFrameInput(frames:Observable<Frame>):FrameBus {
        frames
            // set input delta
            .timeInterval()
            .map { it.value.setDelta(it.intervalInMilliseconds) }
            .subscribe(frameSubject)
        return this
    }

    fun use(middleware:(Frame)->Frame):FrameBus {
        frameMiddlewares.add(middleware)
        return this
    }

    fun use(middlewares:List<(Frame)->Frame>):FrameBus {
        frameMiddlewares.addAll(middlewares)
        return this
    }

    fun postFrame(frame: Frame) {
        frameSubject.onNext(frame)
    }

    fun postFrames(frames:Array<Frame>) {
        frames.forEach { postFrame(it) }
    }
}
