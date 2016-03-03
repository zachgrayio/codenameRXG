package rxg.engine

import rx.Observable
import rx.subjects.BehaviorSubject
import rxg.frame.Frame
import rxg.frame.actor.*
import rxg.input.KeyActions
import rxg.input.KeyEvent
import rxg.input.Keys

interface GameplayEngine {
    /**
     * Every gameplay engine must implement and retain an immutable frame instance
     */
    val framePointer:Frame

    /**
     * Get the actor list from the frame pointer
     */
    val actors: List<Actor> get() = framePointer.actors

    /**
     * The game speed
     */
    val gameSpeed: Float

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
     * Actor extensions & infix operators
     */
    fun actor(init: ActorBuilder.()->Unit): Actor {
        val builder = ActorBuilder()
        builder.init()
        val actor = builder.build()
        return actor
    }

    fun Actor.copy(): Actor {
        val copy = ActorImpl(rotation, size, speedX, speedY, autoReverseEnabled, frameIntervalMs, animations, defaultAnimationKey, false)
        framePointer.actors.add(copy)
        return copy
    }

    infix fun Actor.moveLeft(value:Float): Actor {
        x -= value
            .times(speedX)
            .times(gameSpeed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveRight(value:Float): Actor {
        x += value
            .times(speedX)
            .times(gameSpeed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveUp(value:Float): Actor {
        y -= value
            .times(speedY)
            .times(gameSpeed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.moveDown(value:Float): Actor {
        y += value
            .times(speedY)
            .times(gameSpeed)
            .times(framePointer.delta)
        return this
    }

    infix fun Actor.rotatePositive(value:Float): Actor {
        rotation += value.times(gameSpeed).times(framePointer.delta)
        if(rotation > 360.0f) rotation = 0.0f
        return this
    }

    infix fun Actor.rotateNegative(value:Float): Actor {
        rotation -= value.times(gameSpeed).times(framePointer.delta)
        if(rotation < 0.0f) rotation = 360.0f
        return this
    }

    infix fun Actor.play(key:String): Actor {
        if(previousAnimationKey != null && previousAnimationKey != key)
            previousAnimationKey = currentAnimationKey
        currentAnimationKey = key
        return this
    }

    infix fun Actor.speedX(value:Float): Actor {
        speedX = value
        return this
    }

    infix fun Actor.speedY(value:Float): Actor {
        speedY = value
        return this
    }

    fun Actor.playPrevious(): Actor {
        currentAnimationKey = previousAnimationKey ?: defaultAnimationKey ?: animations.keys.first()
        return this
    }

    fun Actor.playDefault(): Actor {
        if(previousAnimationKey != null && previousAnimationKey != defaultAnimationKey)
            previousAnimationKey = currentAnimationKey

        currentAnimationKey = defaultAnimationKey ?: animations.keys.first()
        return this
    }

    infix fun List<Actor>.applyForce(force:Force) {
        actors.forEach {
            it.apply {
                x += force.x.times(gameSpeed).times(framePointer.delta)
                y += force.y.times(gameSpeed).times(framePointer.delta)
                force.forceClosure(it)
            }
        }
    }

    infix fun Actor.applyForce(force:Force): Actor {
       apply {
           x += force.x.times(gameSpeed).times(framePointer.delta)
           y += force.y.times(gameSpeed).times(framePointer.delta)
           force.forceClosure(this)
        }
        return this
    }

    infix fun Actor.spawn(position: Position): Actor {
        x = position.x
        y = position.y
        framePointer.actors.add(this)
        spawned = true
        return this
    }

    fun Actor.spawn(): Actor {
        spawned = true
        return this
    }

    fun Actor.despawn(): Actor {
        //framePointer.actors.remove(this)
        spawned = false
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

    infix fun List<KeyEvent>.does(closure:()->Any) {
        keySubject
            .filter { event -> any { it.key == event.key && it.keyAction == event.keyAction} }
            .subscribe { closure() }
    }
}