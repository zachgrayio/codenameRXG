package demo

import rxg.frame.Frame
import rxg.gameloop.GameLoop
import rxg.gameloop.gameLoop
import rxg.logger.consoleLogger
import rxg.renderer.AWTIOEngine
import rxg.renderer.GLIOEngine
import rxg.renderer.NopRenderer
import java.util.concurrent.TimeUnit

fun main(args:Array<String>) {
    gameLoop {
        logger {
            consoleLogger(GameLoop::class.java)
        }
        frameRate {
            300
        }
        ioEngine {
            GLIOEngine(1024, 768)
        }
        gameplayEngine {
            DemoGameplayEngine()
        }
        middleware {
            listOf({ frame ->
                //logger?.log("middleware", "Frame delta:${frame.delta}")
                frame
            })
        }
    }
}
