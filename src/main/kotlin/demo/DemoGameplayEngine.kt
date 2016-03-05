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
    val friendSpawnRate:Long = 5000//ms

    // Gameplay states
    //==================================================================================================================
    var paused = false
    var playerGrabbing = false

    // Actors
    //==================================================================================================================
    // extend all actors in this game
    var Actor.health: Int by ActorAttribute({ 3 })
    val Actor.flying : Boolean get() = position.y in 0f..ground
    var Actor.grabbed:Boolean by ActorAttribute({false})
    // define the player
    val player = actor {
        tag = "player"
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

    // Gameplay closures - simple closures can be used to easily extend the gameplay DSL
    //==================================================================================================================
    val gameOver = { player.despawn() }
    val playerStand = { player play "stand" }
    val playerMoveAnimation = { if(player.flying) "swim" else "walk" }
    // define some interval logic to apply to all actors in this game
    val demoGameOnInterval:(Actor)->Unit =
        { if(!paused) {
            if(!it.grabbed) {
                when(it.flying) {
                    true -> it speedX flySpeedX applyForce gravity play "jump"
                    false -> it.playPrevious() speedX walkSpeedX
                }
            }

        }}
    val friendSpawner = doEvery(friendSpawnRate, MILLISECONDS) {
        val friend = actor {
            tag = "friend"
            size = Size(30f, 45f)
            animation(key = "stand") { listOf("mario_stand.gif") }
            animation(key = "crouch", default = true) { listOf("mario_crouch.gif") }
            animation(key = "jump")   { listOf("mario_jump.gif") }
            animation(key = "grabbed") { listOf("mario_skid.gif") }
        }
        friend onInterval demoGameOnInterval spawn Position.random(150f..900f, ground..400f)
    }
    val togglePaused = {
        paused = !paused
        friendSpawner.paused = paused
    }

    // DemoGameplayEngine methods
    //==================================================================================================================

    // Initialization
    //==================================================================================================================
    init {
        // define key bindings
        ESC on RELEASED does togglePaused
        SPACE on PRESSED does { playerGrabbing = true; Unit }
        SPACE on RELEASED does {
            playerGrabbing = false
            actors.forEach { it.grabbed = false }
        }
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
        player onCollision { other ->
            when(other.tag) {
                "friend" -> {
                    if(playerGrabbing) {
                        other play "grabbed"
                        other.latchTo(player, whileTrue = { playerGrabbing })
                        other.grabbed = true
                    }
                }
            }
        }

        // initialize game
        // plants
        for(index in 0..18) {
            val plant = actor {
                tag = "plant"
                size = Size(30f, 40f)
                animation("", default = true) { listOf("piranha_plant.gif") }
            }
            plant onCollision { other -> other.despawn() } spawn Position(index * 45f + 100f, ground)
        }
        // pipe
        actor {
            tag = "pipe"
            size = Size(80f, 60f)
            animation("", default = true) { listOf("pipe.gif") }
        } onCollision { other -> if(other.tag == "friend") other.despawn() } spawn Position(980f, ground)
        // player
        player spawn Position(15f, ground)
    }
}