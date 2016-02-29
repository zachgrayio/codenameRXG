package rxg.frame.actor

import java.util.*

interface Actor {
    var x:Float
    var y:Float
    var rotation:Float
    var size: Size
    var speedX:Float
    var speedY:Float
    val autoReverseEnabled:Boolean
    val frameIntervalMs:Long
    val animations: HashMap<String, List<String>>
    var currentAnimationKey:String?
    var defaultAnimationKey:String?
    var previousAnimationKey:String?
    var reverseSprite: Boolean
    var spawned: Boolean
    fun currentSprite():String
}