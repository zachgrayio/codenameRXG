package demo

import rxg.frame.Position
import rxg.frame.actor.Actor
import rxg.frame.actor.ActorAttribute
import rxg.frame.actor.actor
import rxg.input.KeyActions.*
import rxg.input.Keys.*
import rxg.preset.SimpleGameplayEngine

/**
 * A simple gameplay system based on an interval-based engine which generates 1,000 gameplay frames each second.
 */
class DemoGameplayEngine() : SimpleGameplayEngine() {

    // Actors
    //==================================================================================================================
    val player = actor {
        size = Actor.Size(50f,50f)
        speed = 0.5f
        frameIntervalMs = 100
        animation("stand", default = true) {
            listOf("mario_stand.gif")
        }
        animation("walk") {
            listOf("mario_walk_1.gif", "mario_walk_2.gif", "mario_walk_3.gif")
        }
        animation("crouch") {
            listOf("mario_crouch.gif")
        }
    }
    // 3 hit points!
    var Actor.health: Int by ActorAttribute(initialValue = 3)

    // Gameplay settings
    //==================================================================================================================
    val step = 1f

    // Gameplay states
    //==================================================================================================================
    var paused = false

    // Gameplay functions - simple closures can be used to easily extend the gameplay DSL
    //==================================================================================================================
    val notPaused:(()->Unit ) -> () -> Unit = { if(!paused) it else { {} } }
    val togglePaused    = { paused = !paused }
    val gameOver        = { player.despawn() }
    val jump            = { }
    val stand           = { player play "stand" }
    val crouch          = notPaused { player play "crouch" }
    val moveLeft        = notPaused { player moveLeft step play "walk" }
    val moveRight       = notPaused { player moveRight step play "walk" }

    // Update
    //==================================================================================================================
    override fun update() {
        when(player.health) { 0 -> gameOver() }
    }

    // Initialization
    //==================================================================================================================
    init {
        // define key bindings
        ESC on RELEASED does togglePaused

        W on PRESSED does jump

        A on PRESSED does moveLeft
        A on RELEASED does stand

        S on PRESSED does crouch
        S on RELEASED does stand

        D on PRESSED does moveRight
        D on RELEASED does stand

        // initialize game
        player spawn Position(200f, 200f)
    }
}