package demo

import rxg.frame.actor.*
import rxg.input.KeyActions.*
import rxg.input.Keys.*
import rxg.preset.SimpleGameplayEngine

/**
 * A simple gameplay system based on an interval-based engine which generates 1,000 gameplay frames each second.
 */
class DemoGameplayEngine() : SimpleGameplayEngine() {

    // Actors
    //==================================================================================================================
    // extend all actors in this game
    var Actor.health: Int by ActorAttribute(initialValue = 3)
    var Actor.falling: Boolean by ActorAttribute(initialValue = false)

    // define the player
    val player = actor {
        size = Size(50f, 50f)
        speed = 0.5f
        frameIntervalMs = 100
        autoReverseEnabled = true
        animation("stand", default = true) { listOf("fire_mario_stand.gif") }
        animation("walk")   { listOf("fire_mario_walk_1.gif", "fire_mario_walk_2.gif", "fire_mario_walk_3.gif") }
        animation("crouch") { listOf("fire_mario_crouch.gif") }
        animation("jump")   { listOf("fire_mario_jump.gif") }
        animation("swim")   {
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
    // define some goons
    val guy1 = actor {
        size = Size(50f, 50f)
        animation("stand") { listOf("mario_stand.gif") }
        animation("crouch", default = true) { listOf("mario_crouch.gif") }
        animation("jump")   { listOf("mario_jump.gif") }
    }
    val guy2 = actor {
        size = Size(50f, 50f)
        animation("stand") { listOf("mario_stand.gif") }
        animation("crouch", default = true) { listOf("mario_crouch.gif") }
        animation("jump")   { listOf("mario_jump.gif") }
    }
    val guy3 = actor {
        size = Size(50f, 50f)
        animation("stand") { listOf("mario_stand.gif") }
        animation("crouch", default = true) { listOf("mario_crouch.gif") }
        animation("jump")   { listOf("mario_jump.gif") }
    }

    // Gameplay settings
    //==================================================================================================================
    val step = 1f
    val ground = 768f // until collision detection is supported, this will have to do
    val gravity = Force(y = 0.2f)

    // Gameplay states
    //==================================================================================================================
    var paused = false

    // Gameplay functions - simple closures can be used to easily extend the gameplay DSL
    //==================================================================================================================
    val notPaused:(()->Unit ) -> () -> Unit = { if(!paused) it else { {} } }
    val togglePaused    = { paused = !paused }
    val gameOver        = { player.despawn() }

    val stand = { player play "stand" }
    val playerMoveAnimation = { if(player.falling) "swim" else "walk" }

    // Update
    //==================================================================================================================
    override fun update() {
        actors.forEach {
            when(it.y) {
                in 0f..ground -> it.falling = true
                else -> it.falling = false
            }
            when(it.falling) {
                true -> it applyForce gravity play "jump"
                false -> it.playPrevious()
            }
        }
        when(player.health) { 0 -> gameOver() }
    }

    // Initialization
    //==================================================================================================================
    init {
        // define key bindings
        ESC on RELEASED does togglePaused

        SPACE on PRESSED does { /* todo: jump */ }

        W on PRESSED does notPaused { player moveUp step play "swim" }

        A on PRESSED does notPaused { player moveLeft step play playerMoveAnimation() }
        A on RELEASED does stand

        S on PRESSED does notPaused { player play "crouch" }
        S on RELEASED does stand

        D on PRESSED does notPaused { player moveRight step play playerMoveAnimation() }
        D on RELEASED does stand

        // initialize game
        player spawn Position(25f, ground)
        guy1 spawn Position(500f, ground - 400f)
        guy2 spawn Position(200f, ground - 300f)
        guy3 spawn Position(700f, ground - 500f)
    }
}