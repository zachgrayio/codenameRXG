package rxg.engine

import rx.Observable
import rx.subjects.BehaviorSubject
import rxg.frame.Frame
import rxg.frame.Position
import rxg.frame.actor.Actor
import rxg.input.KeyActions
import rxg.input.KeyEvent
import rxg.input.Keys

interface GameplayEngine {
    /**
     * Every gameplay engine must implement and retain an immutable frame instance
     */
    val framePointer:Frame

    /**
     * A gameplay engine should have a key subject
     */
    val keySubject: BehaviorSubject<KeyEvent>

    /**
     * An observable stream of frames emitted by the gameplay engine
     */
    fun frameOutput(): Observable<Frame>

    /**
     * Bind an observable stream of key events to the key subject
     */
    fun bindKeyInput(keyEvents: Observable<KeyEvent>) { keyEvents.subscribe(keySubject) }

    /**
     * Get the actor list from the frame pointer
     */
    fun actors(): List<Actor> = framePointer.actors

    /**
     * Actor infix operators
     */
    infix fun Actor.moveLeft(value:Float): Actor {
        x -= value
            .times(speed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveRight(value:Float): Actor {
        x += value
            .times(speed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveUp(value:Float): Actor {
        y -= value
            .times(speed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveDown(value:Float): Actor {
        y += value
            .times(speed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.rotatePositive(value:Float): Actor {
        rotation += value.times(framePointer.delta)
        if(rotation > 360.0f) rotation = 0.0f
        return this
    }

    infix fun Actor.rotateNegative(value:Float): Actor {
        rotation -= value.times(framePointer.delta)
        if(rotation < 0.0f) rotation = 360.0f
        return this
    }

    infix fun Actor.play(value:String): Actor {
        currentAnimationKey = value
        return this
    }

    /**
     * Actor spawn / despawn
     */
    infix fun Actor.spawn(position: Position): Actor {
        x = position.x
        y = position.y
        framePointer.actors.add(this)
        return this
    }

    fun Actor.spawn(): Actor {
        framePointer.actors.add(this)
        return this
    }

    fun Actor.despawn(): Actor {
        framePointer.actors.remove(this)
        return this
    }

    /**
     * Key subject infix operators
     */
    infix fun Keys.on(action:KeyActions): KeyEvent {
        return KeyEvent(this, action)
    }

    infix fun KeyEvent.or(action:KeyActions): MutableList<KeyEvent> {
        return mutableListOf(this, KeyEvent(key, action))
    }

    infix fun MutableList<KeyEvent>.or(action:KeyActions): MutableList<KeyEvent> {
        this.add(KeyEvent(get(0).key, action))
        return this
    }

    infix fun KeyEvent.does(closure:()->Any) {
        keySubject
            .filter { it.key == key && it.keyAction == keyAction}
            .subscribe { closure() }
    }

    infix fun MutableList<KeyEvent>.does(closure:()->Any) {
        keySubject
            .filter { event -> any { it.key == event.key && it.keyAction == event.keyAction} }
            .subscribe { closure() }
    }
}