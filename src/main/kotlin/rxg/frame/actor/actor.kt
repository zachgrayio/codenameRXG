package rxg.frame.actor

import java.util.*
import kotlin.reflect.KProperty

class ActorAttribute<T: Any>(private var initialValue:T) {
    private var valueMap = HashMap<Actor, T>()
    operator fun getValue(actor: Actor, property: KProperty<*>): T {
        if(valueMap[actor] == null) valueMap.put(actor, initialValue)
        return valueMap[actor]!!
    }
    operator fun setValue(actor: Actor, property: KProperty<*>, value: T) { valueMap.put(actor, value) }
}

fun actor(init: ActorBuilder.()->Unit): Actor {
    val builder = ActorBuilder()
    builder.init()
    val actor = builder.build()
    return actor
}

data class Position(var x:Float, var y:Float)
data class Size(var x:Float = 0f, var y:Float = 0f)
data class Force(var x:Float = 0f, var y:Float = 0f, val forceClosure:Actor.() -> Unit = {})