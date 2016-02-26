package rxg.resource

import java.awt.image.BufferedImage

interface ResourceManager {
    fun getBufferedImageSprite(filename:String): BufferedImage
    fun getByteBufferSprite(filename:String): ByteBufferImage
}
