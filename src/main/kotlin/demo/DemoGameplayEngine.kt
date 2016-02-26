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
    val squad = actor {
        size = Actor.Size(50f,50f)
        speed = 1f
        frameIntervalMs = 100
        sprites = listOf("squad.png")
    }
    // all actors in this game should have health, so lets add an attribute to Actor:
    var Actor.health: Int by ActorAttribute(initialValue = 100)

    // Gameplay settings
    //==================================================================================================================
    val step = 1f

    // Gameplay states
    //==================================================================================================================
    var paused = false

    // Gameplay functions - simple closures can be used to easily extend the gameplay DSL
    //==================================================================================================================
    val togglePaused    = { paused = !paused }
    val gameOver        = { squad.despawn() }
    val moveSquadUp     = { if(!paused) squad moveUp    step }
    val moveSquadDown   = { if(!paused) squad moveDown  step }
    val moveSquadLeft   = { if(!paused) squad moveLeft  step }
    val moveSquadRight  = { if(!paused) squad moveRight step }
    val rotateSquadPos  = { if(!paused) squad rotatePositive 0.1f }
    val rotateSquadNeg  = { if(!paused) squad rotateNegative 0.1f }

    // Update
    //==================================================================================================================
    override fun update() {
        when(squad.health) { 0 -> gameOver() }
    }

    // Initialization
    //==================================================================================================================
    init {
        // define key bindings
        ESC on RELEASED does togglePaused
        W on PRESSED does moveSquadUp
        A on PRESSED does moveSquadLeft
        S on PRESSED does moveSquadDown
        D on PRESSED does moveSquadRight
        Q on PRESSED does rotateSquadNeg
        E on PRESSED does rotateSquadPos
        // initialize game
        squad spawn Position(200f, 200f)
    }
}