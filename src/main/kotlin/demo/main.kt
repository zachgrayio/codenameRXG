package demo

import rxg.gameloop.GameLoop
import rxg.gameloop.gameLoop
import rxg.logger.consoleLogger
import rxg.renderer.GLIOEngine

fun main(args:Array<String>) {
    gameLoop {
        logger {
            consoleLogger(GameLoop::class.java)
        }
        frameRate {
            60
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
