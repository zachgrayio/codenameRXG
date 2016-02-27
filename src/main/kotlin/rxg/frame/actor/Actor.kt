package rxg.frame.actor

import rxg.frame.Size
import java.util.*

interface Actor {
    var x:Float
    var y:Float
    var rotation:Float
    val size: Size
    val speed:Float
    val autoReverseEnabled:Boolean
    val frameIntervalMs:Long
    val animations: HashMap<String, List<String>>
    var currentAnimationKey:String?
    var reverseSprite: Boolean
    fun currentSprite():String
}