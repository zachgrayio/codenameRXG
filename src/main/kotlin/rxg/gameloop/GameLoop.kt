package rxg.gameloop

import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rxg.frame.FrameBus
import rxg.engine.GameplayEngine
import rxg.frame.Frame
import rxg.logger.Logger
import rxg.renderer.IOEngine
import java.util.concurrent.TimeUnit

class GameLoop(var logger: Logger?,
               private var frameRate:Long,
               private var ioEngine: IOEngine,
               private var gameplayEngine: GameplayEngine,
               private var middlewares:List<(Frame)->Frame>) {

    init {
        printConfig()
        val exitSignal = BehaviorSubject.create<Void>()
        var mainSignal: Observable<Unit?>?
        var mainSubscription: Subscription? = null
        try {
            gameplayEngine.bindKeyInput(ioEngine.keyOutput())

            // compose main signal
            mainSignal = FrameBus()
                .bindFrameInput(gameplayEngine.frameOutput())       // use the configured gameplay engine as framebus input
                .use(middlewares)                                   // apply configured middleware
                .frameOutput()                                      // get the Observable<Frame> output
                .sample(1000 / frameRate, TimeUnit.MILLISECONDS)    // sample output @ the configured framerate
                .map { frame -> ioEngine.render(frame) }            // map frames to renderer

            // subscribe to main signal until exit signal is received
            mainSubscription = mainSignal!!
                .takeUntil(exitSignal)
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe()

            logger?.log("run", "Main subscription active, game loop is running.")

            /////// (block here until exit signal is received)
            exitSignal.toBlocking().first()
        }
        catch(e:Exception) { logger?.log("init", "Failed to start game loop: ${e.message}") }
        finally {
            ioEngine.close()
            mainSubscription?.unsubscribe()
        }
    }

    fun printConfig() {
        logger?.log("init","FrameBus:")
        logger?.log("init", "- input bound to engine %s".format(gameplayEngine.javaClass.name))
        logger?.log("init", "- middleware count: %d".format(middlewares.count()))
        logger?.log("init", "- framebus output sampling @ %d FPS".format(frameRate))
        logger?.log("init", "- framebus output bound to renderer %s".format(ioEngine.javaClass.name))
        logger?.log("init", "Initialization complete.")
    }
}