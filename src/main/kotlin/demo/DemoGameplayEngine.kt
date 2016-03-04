package demo

import rxg.frame.actor.*
import rxg.gameplay.dsl.doEvery
import rxg.gameplay.dsl.ifNot
import rxg.input.KeyActions.*
import rxg.input.Keys.*
import rxg.logger.consoleLogger
import rxg.preset.SimpleGameplayEngine
import java.util.concurrent.TimeUnit.*

/**
 * A simple gameplay system based on an interval-based engine which generates 1,000 gameplay frames each second.
 */
class DemoGameplayEngine() : SimpleGameplayEngine() {

    // Logger
    val logger = consoleLogger(javaClass)

    // Gameplay settings
    //==================================================================================================================
    override val gameSpeed: Float = 1.0f
    val step = 1f
    val walkSpeedX = 0.4f
    val flySpeedX = 0.2f
    val flySpeedY = 0.3f
    val ground = 768f
    val gravity = Force(y = 0.2f)
    val friendSpawnRate:Long = 1000//ms

    // Gameplay states
    //==================================================================================================================
    var paused = false

    // Actors
    //==================================================================================================================
    // extend all actors in this game
    var Actor.health: Int by ActorAttribute({ 3 })
    val Actor.flying : Boolean get() = y in 0f..ground
    // define the player
    val player = actor {
        size = Size(30f, 60f)
        speedX = walkSpeedX
        speedY = flySpeedY
        frameIntervalMs = 100
        autoReverseEnabled = true
        animation(key = "stand", default = true) { listOf("fire_mario_stand.gif") }
        animation(key = "walk")   { listOf("fire_mario_walk_1.gif", "fire_mario_walk_2.gif", "fire_mario_walk_3.gif") }
        animation(key = "crouch") { listOf("fire_mario_crouch.gif") }
        animation(key = "jump")   { listOf("fire_mario_jump.gif") }
        animation(key = "swim")   {
            listOf(
                "fire_mario_swim_1.gif",
                "fire_mario_swim_2.gif",
                "fire_mario_swim_3.gif",
                "fire_mario_swim_4.gif",
                "fire_mario_swim_5.gif",
                "fire_mario_swim_6.gif"
            )
        }
        health = 3
    }
    // define a friendly actor
    //val friend =

    // Gameplay closures - simple closures can be used to easily extend the gameplay DSL
    //==================================================================================================================

    val togglePaused = {
        paused = !paused
        //friendSpawner.paused = paused
    }
    val gameOver = { player.despawn() }
    val playerStand = { player play "stand" }
    val playerMoveAnimation = { if(player.flying) "swim" else "walk" }
    // define some interval logic to apply to all actors in this game
    val demoGameOnInterval:(Actor)->Unit =
        { if(!paused) {
            when(it.flying) {
            true -> it speedX flySpeedX applyForce gravity play "jump"
            false -> it.playPrevious() speedX walkSpeedX
            }
        }}
    // DemoGameplayEngine methods
    //==================================================================================================================

    // Initialization
    //==================================================================================================================
    init {
        // define key bindings
        ESC on RELEASED does togglePaused
        SPACE on PRESSED does { /* todo: GRAB friendly unit */ }
        W on PRESSED does ifNot(paused) { player    moveUp step         play "swim" }
        A on PRESSED does ifNot(paused) { player    moveLeft step       play playerMoveAnimation() }
        S on PRESSED does ifNot(paused) { player    play "crouch" }
        D on PRESSED does ifNot(paused) { player    moveRight step      play playerMoveAnimation() }
        A on RELEASED does playerStand // todo: 'or' infix for keys
        S on RELEASED does playerStand
        D on RELEASED does playerStand

        // define player behavior and collision logic
        player onInterval { if(it.health <= 0) gameOver() }
        player onInterval demoGameOnInterval
        player onCollision { other -> other.despawn() }

        // initialize game
        player spawn Position(25f, ground)

        for(it in 1..1000) {
        //doEvery(500, MILLISECONDS) {
            //if(it > 4)
            actor {
                size = Size(30f, 45f)
                animation(key = "stand") { listOf("mario_stand.gif") }
                animation(key = "crouch", default = true) { listOf("mario_crouch.gif") }
                animation(key = "jump")   { listOf("mario_jump.gif") }
            } onInterval demoGameOnInterval spawn Position(it * 10f, ground-400f)//Position.random(xMax = 1000f, yMax = ground - 400)
        }
    }
}