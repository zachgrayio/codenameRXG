package rxg.frame.actor

class ActorBuilder {
    var x:Float = 0f
    var y:Float = 0f
    var size:Actor.Size = Actor.Size()
    var speed:Float = 0f
    var frameIntervalMs:Long = 100
    var sprites: List<String> = listOf()

    fun build(): Actor {
        return Actor(x, y, size, speed, frameIntervalMs, sprites)
    }
}