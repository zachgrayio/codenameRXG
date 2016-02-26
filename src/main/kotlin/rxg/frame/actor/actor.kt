package rxg.frame.actor

import kotlin.reflect.KProperty

class ActorAttribute<T: Any>(initialValue:T) {
    private var value:T = initialValue
    operator fun getValue(actor: Actor, property: KProperty<*>): T { return value }
    operator fun setValue(actor: Actor, property: KProperty<*>, value: T) { this.value = value }
}

fun actor(init: ActorBuilder.()->Unit): Actor {
    val builder = ActorBuilder()
    builder.init()
    val actor = builder.build()
    return actor
}