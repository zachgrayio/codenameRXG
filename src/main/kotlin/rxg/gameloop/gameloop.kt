package rxg.gameloop

fun gameLoop(init: GameLoopBuilder.() -> Unit) : GameLoop {
    val builder = GameLoopBuilder()
    builder.init()
    return builder.build()
}