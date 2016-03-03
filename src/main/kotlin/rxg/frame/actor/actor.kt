package rxg.frame.actor

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.reflect.KProperty
import rxg.common.randomFloatBetween

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

data class Position(var x:Float, var y:Float) {
    companion object {
        fun random(xMax:Float = 0f, yMax:Float = 0f): Position {
            val randX = randomFloatBetween(0f, xMax)
            val randY = randomFloatBetween(0f, yMax)
            return Position(x = randX, y = randY)
        }
    }
}

data class Size(var x:Float = 0f, var y:Float = 0f)
data class Force(var x:Float = 0f, var y:Float = 0f, val forceClosure:Actor.() -> Unit = {})