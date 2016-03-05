package rxg.preset

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rxg.gameplay.engine.IntervalGameplayEngine
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
        val actorsCopy = framePointer.actors.toList()
        actorsCopy.forEach { actor ->
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
    private var collisionChecker = doEvery(10, TimeUnit.MILLISECONDS) {
        val oactor = Observable.from(framePointer.actors.toList()).cache()
        oactor
            .filter { it.spawned }
            .forEach { actor ->
                oactor.forEach { if(actor.isColliding(it)) collisionSubject.onNext(Pair(actor, it)) }
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