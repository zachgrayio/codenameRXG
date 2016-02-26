package rxg.resource

import rxg.logger.consoleLogger
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class ResourceManagerImpl : ResourceManager {
    private val logger = consoleLogger(javaClass)
    private val bufferedImageSpriteMap = Hashtable<String, BufferedImage>()
    private val byteBufferSpriteMap = Hashtable<String, ByteBufferImage>()

    override fun getBufferedImageSprite(filename: String):BufferedImage {
        return bufferedImageSpriteMap[filename]!!
    }

    override fun getByteBufferSprite(filename: String): ByteBufferImage {
        return byteBufferSpriteMap[filename]!!
    }

    init {
        logger.log("init", "Loading resources...")
        var loaded = 0
        val start = System.nanoTime()
        Files.walk(Paths.get("res/sprite/"))
            .filter { Files.isRegularFile(it) }
            .forEach {
                try {
                    val key = it.fileName.toString()
                    bufferedImageSpriteMap.put(key, ImageIO.read(File(it.toAbsolutePath().toString())))
                    byteBufferSpriteMap.put(key, ByteBufferImage(it.toAbsolutePath().toString()))
                    loaded++
                } catch (e: Exception) {
                    logger.log("init", "Error loading sprite: ${e.message}")
                }
            }

        // todo
//        Files.walk(Paths.get("res/audio/"))
//            .filter { Files.isRegularFile(it) }

        val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        logger.log("init", "Done. Loaded $loaded resource files after $duration ms.")
    }
}