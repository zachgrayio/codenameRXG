package rxg.gameloop

import rxg.engine.GameplayEngine
import rxg.frame.Frame
import rxg.logger.Logger
import rxg.logger.createLogger
import rxg.renderer.IOEngine

class GameLoopBuilder {

    fun build() : GameLoop {
        // create a build logger using the print func of the configured logger, but with an accurate label
        val loggerLabel = this.javaClass.name
        val buildLogger = createLogger {
            label { loggerLabel }
            printFunc { logger?.printFunc!! }
        }
        buildLogger.log("build", "Starting game loop...")
        val gameLoop = GameLoop(logger, frameRate!!, IOEngine!!, engine!!, middlewares)
        return gameLoop
    }

    /**
     * logger
     */
    var logger: Logger? = null
    fun logger(loggerClosure:() -> Logger) {
        logger = loggerClosure()
    }

    /**
     * frameRate
     */
    var frameRate:Long? = null
    fun frameRate(frameRateClosure:() -> Long) {
        frameRate = frameRateClosure()
    }

    /**
     * frameRenderer
     */
    var IOEngine: IOEngine? = null
    fun ioEngine(IOEngineClosure:() -> IOEngine) {
        IOEngine = IOEngineClosure()
    }

    /**
     * engine
     */
    var engine: GameplayEngine? = null
    fun gameplayEngine(engineClosure:() -> GameplayEngine) {
        engine = engineClosure()
    }

    /**
     * frame middleware
     */
    var middlewares: List<(Frame) -> Frame> = mutableListOf()
    fun middleware(middlewareClosure:() -> List<(Frame) -> Frame>) {
        middlewares = middlewareClosure()
    }
}