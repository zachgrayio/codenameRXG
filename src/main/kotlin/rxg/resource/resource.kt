package rxg.resource

import org.lwjgl.BufferUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels

/**
 * Reads the specified resource and returns the raw data as a ByteBuffer.
 *
 * @param path the resource to read
 * @param bufferSize the initial buffer size
 *
 * @return the resource data
 *
 * @throws IOException if an IO error occurs
 */
@Throws(IOException::class)
internal fun resourceToByteBuffer(path:String, bufferSize:Int): ByteBuffer {
    return when(File(path).isFile) {
        true -> flippedByteBufferFromFilePath(path)
        false -> flippedByteBufferFromResourcePath(path, bufferSize)
    }
}

private fun flippedByteBufferFromFilePath(path:String): ByteBuffer {
    val fis = FileInputStream(File(path))
    val fc = fis.channel
    val buffer = BufferUtils.createByteBuffer(fc.size().toInt() + 1)
    while (fc.read(buffer) != -1) ;
    fis.close()
    fc.close()
    return buffer.flip() as ByteBuffer
}

private fun flippedByteBufferFromResourcePath(path:String, bufferSize: Int): ByteBuffer{
    var buffer = BufferUtils.createByteBuffer(bufferSize)
    val source = Thread.currentThread().contextClassLoader.getResourceAsStream(path) ?: throw FileNotFoundException(path)
    try {
        val rbc = Channels.newChannel(source)
        try {
            while (true) {
                val bytes = rbc.read(buffer)
                if (bytes == -1) break
                if (buffer.remaining() == 0) buffer = resizeBuffer(buffer, buffer.capacity() * 2)
            }
        }
        finally { rbc.close() }
    }
    finally { source.close() }
    return buffer.flip() as ByteBuffer
}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity:Int): ByteBuffer {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}
