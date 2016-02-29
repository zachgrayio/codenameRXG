package rxg.frame.actor

import java.util.*
import kotlin.reflect.KProperty

class ActorAttribute<T: Any>(private var initialValueClosure: ()->T) {
    private var valueMap = HashMap<Int, T>()
    operator fun getValue(actor: Actor, property: KProperty<*>): T {
        if(valueMap[actor.hashCode()] == null) valueMap.put(actor.hashCode(), initialValueClosure())
        return valueMap[actor.hashCode()]!!
    }
    operator fun setValue(actor: Actor, property: KProperty<*>, value: T) {
        valueMap.put(actor.hashCode(), value)
    }
}

data class Position(var x:Float, var y:Float)
data class Size(var x:Float = 0f, var y:Float = 0f)
data class Force(var x:Float = 0f, var y:Float = 0f, val forceClosure:Actor.() -> Unit = {})