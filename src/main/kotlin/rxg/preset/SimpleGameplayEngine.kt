package rxg.preset

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rxg.engine.IntervalGameplayEngine
import rxg.frame.Frame
import rxg.frame.actor.Actor
import rxg.frame.actor.ActorAttribute
import rxg.gameplay.dsl.doEvery
import rxg.input.KeyEvent
import java.util.concurrent.TimeUnit

abstract class SimpleGameplayEngine : IntervalGameplayEngine {
    override val timeUnit: TimeUnit get() = TimeUnit.MILLISECONDS
    override val interval: Long get() = 1
    override val keySubject: BehaviorSubject<KeyEvent> = BehaviourSubject()
    override val framePointer = Frame()

    /**
     * Actor - interval behavior
     */
    private var Actor.intervalClosures: MutableList<(Actor)->Unit> by ActorAttribute<MutableList<(Actor)->Unit>>(initialValueClosure = { mutableListOf() })
    override fun onInterval() {
        val despawned: MutableList<Actor> = mutableListOf()
        framePointer.actors.forEach { actor ->
            actor.intervalClosures.forEach { it(actor) }
            if(!actor.spawned) despawned.add(actor)
        }
        despawned.forEach { framePointer.actors.remove(it) }
    }
    infix fun Actor.onInterval(closure:(Actor)->Unit): Actor {
        intervalClosures.add(closure)
        return this
    }
    infix fun List<Actor>.onInterval(closure:(Actor)->Unit): List<Actor> {
        forEach { it.intervalClosures.add(closure) }
        return this
    }

    /**
     * Actor - collisions
     */
    private var collisionChecker = doEvery(50, TimeUnit.MILLISECONDS) { // check for collisions every 50 ms on a new thread
        val actorsCopy = framePointer.actors.filter { it.spawned }.toList()
        actorsCopy.forEach { actor ->
            actorsCopy.forEach { if(actor.isColliding(it)) collisionSubject.onNext(Pair(actor, it)) }
        }
    }
    private var collisionSubject: BehaviorSubject<Pair<Actor, Actor>> = BehaviourSubject()
    val collisions: Observable<Pair<Actor, Actor>> get() = collisionSubject.asObservable()

    infix fun Actor.onCollision(collisionClosure:(Actor)->Unit): Actor {
        collisions
            .filter { it.first === this || it.second === this }
            .subscribe { collisionClosure(if(it.first === this) it.second else it.first) }
        return this
    }
}