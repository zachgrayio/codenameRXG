package rxg.preset

import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.subjects.BehaviorSubject
import rxg.engine.IntervalGameplayEngine
import rxg.frame.Frame
import rxg.frame.actor.Actor
import rxg.frame.actor.ActorAttribute
import rxg.input.KeyEvent
import java.util.concurrent.TimeUnit

abstract class SimpleGameplayEngine : IntervalGameplayEngine {
    override val timeUnit: TimeUnit get() = TimeUnit.MILLISECONDS
    override val interval: Long get() = 10
    override val keySubject: BehaviorSubject<KeyEvent> = BehaviourSubject()
    override val framePointer = Frame()
    private var collisionSubject: BehaviorSubject<Pair<Actor, Actor>> = BehaviourSubject()
    val collisions: Observable<Pair<Actor, Actor>> get() = collisionSubject.asObservable()
    var Actor.intervalClosures: MutableList<(Actor)->Unit> by ActorAttribute<MutableList<(Actor)->Unit>>(initialValueClosure = { mutableListOf() })

    override fun onInterval() {
        framePointer.actorCollisions()
            .distinct()
            .filter { it.first.spawned and it.second.spawned }
            .forEach { collisionSubject.onNext(it) }
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

    infix fun Actor.onCollision(collisionClosure:(Actor)->Unit): Actor {
        collisions
            .filter { it.first.spawned and it.second.spawned }
            .filter { it.first === this || it.second === this }
            .subscribe { collisionClosure(if(it.first === this) it.second else it.first) }
        return this
    }
}