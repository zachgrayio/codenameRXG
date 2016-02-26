package rxg.frame

class ActorBuilder {
    var x:Float = 0f
    var y:Float = 0f
    var speed:Float = 0f
    var frameIntervalMs:Long = 100
    var sprites: List<String> = listOf()
    fun build(): Actor {
        return Actor(x, y, speed, frameIntervalMs, sprites)
    }
}