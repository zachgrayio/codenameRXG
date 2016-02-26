package rxg.resource

import org.lwjgl.BufferUtils
import java.io.IOException
import java.nio.ByteBuffer
import org.lwjgl.stb.STBImage.*

class ByteBufferImage(val path:String) {
    private val widthBuffer = BufferUtils.createIntBuffer(1)
    private val heightBuffer = BufferUtils.createIntBuffer(1)
    private val compBuffer = BufferUtils.createIntBuffer(1)

    var image: ByteBuffer? = null
    var width = 0
    var height = 0
    var comp = 0

    init {
        try {
            image = stbi_load_from_memory(resourceToByteBuffer(path, 8 * 1024), widthBuffer, heightBuffer, compBuffer, 0)
        } catch (e: IOException) {
            throw RuntimeException(e);
        }
        if(image == null) throw RuntimeException("Failed to load image: " + stbi_failure_reason())
        width = widthBuffer[0]
        height = heightBuffer[0]
        comp = compBuffer[0]
    }
}