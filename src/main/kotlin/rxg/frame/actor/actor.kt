package rxg.frame.actor

import java.util.*
import kotlin.reflect.KProperty
import rxg.common.randomFloatInRange

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
        fun random(xBounds:ClosedRange<Float>, yBounds:ClosedRange<Float>): Position {
            val randX = randomFloatInRange(xBounds)
            val randY = randomFloatInRange(yBounds)
            return Position(x = randX, y = randY)
        }
    }
}

data class Size(var x:Float = 0f, var y:Float = 0f)
data class Force(var x:Float = 0f, var y:Float = 0f)

fun Actor.copy(): Actor {
    val copy = ActorImpl(rotation, size, speedX, speedY, autoReverseEnabled, frameIntervalMs, animations, defaultAnimationKey, false)
    return copy
}