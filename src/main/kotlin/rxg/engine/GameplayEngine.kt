package rxg.engine

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.subjects.BehaviorSubject
import rxg.frame.Actor
import rxg.frame.ActorBuilder
import rxg.frame.Frame
import rxg.input.KeyActions
import rxg.input.KeyEvent
import rxg.input.Keys
import rxg.resource.ResourceManager
import rxg.resource.ResourceManagerImpl

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
     * Actor infix operators
     */
    infix fun Actor.moveLeft(value:Float) {
        this.x -= value
            .times(speed)
            .times(framePointer.delta)
    }

    infix fun Actor.moveRight(value:Float) {
        this.x += value
            .times(speed)
            .times(framePointer.delta)
    }

    infix fun Actor.moveUp(value:Float) {
        this.y -= value
            .times(speed)
            .times(framePointer.delta)
    }

    infix fun Actor.moveDown(value:Float) {
        this.y += value
            .times(speed)
            .times(framePointer.delta)
    }

    /**
     * Key subject infix operators
     */
    infix fun Keys.When(action:KeyActions):KeyEvent {
        return KeyEvent(this, action)
    }

    infix fun KeyEvent.or(action:KeyActions):MutableList<KeyEvent> {
        return mutableListOf(this, KeyEvent(this.key, action))
    }

    infix fun MutableList<KeyEvent>.or(action:KeyActions):MutableList<KeyEvent> {
        this.add(KeyEvent(get(0).key, action))
        return this
    }

    infix fun KeyEvent.does(closure:()->Unit) {
        keySubject
            .filter { it.key == this.key && it.keyAction == this.keyAction}
            .subscribe { closure() }
    }

    infix fun MutableList<KeyEvent>.does(closure:()->Unit) {
        keySubject
            .filter { event -> this.any { it.key == event.key && it.keyAction == event.keyAction} }
            .subscribe { closure() }
    }

    fun actor(init: ActorBuilder.()->Unit):Actor {
        val builder = ActorBuilder()
        builder.init()
        val actor = builder.build()
        framePointer.actors.add(actor)
        return actor
    }
}