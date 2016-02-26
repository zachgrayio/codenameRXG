package rxg.renderer

import rxg.frame.Frame
import org.lwjgl.*
import org.lwjgl.glfw.*
import org.lwjgl.opengl.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*
import rx.Observable
import rx.lang.kotlin.BehaviourSubject
import rx.subjects.BehaviorSubject
import rxg.input.Keys
import rxg.input.KeyActions
import rxg.input.KeyEvent
import rxg.resource.ResourceManager
import rxg.resource.ResourceManagerDelegate

/**
 * An LWJGL 2D renderer implementation
 */
class GLIOEngine(override val width: Int, override val height: Int) : IOEngine {
    override val resourceManager: ResourceManager by ResourceManagerDelegate()

    private val keySubject:BehaviorSubject<KeyEvent> = BehaviourSubject()

    private var ready:Boolean = false
    private var window:Long? = null
    private var glCapabilities: GLCapabilities? = null

    private val errorCallback:GLFWErrorCallback = GLFWErrorCallback.createPrint(System.err)

    /**
     * Adapts GLFW key presses to rxg.input.KeyEvents and posts them to the key subject
     */
    private val keyCallback:GLFWKeyCallback? = object : GLFWKeyCallback() {
        override operator fun invoke(window: kotlin.Long, key: kotlin.Int, scancode: kotlin.Int, action: kotlin.Int, mods: kotlin.Int) {
            keySubject.onNext(KeyEvent(Keys.from(key), KeyActions.from(action)))
        }
    }

    init {
        try { initGL() }
        catch (e:Exception) {
            close()
            e.printStackTrace()
        }
    }

    override fun close() {
        ready = false
        glDisable(GL_TEXTURE_2D)
        if(window != null) {
            glfwDestroyWindow(window!!)
        }
        keyCallback?.release()
        errorCallback.release()
        glfwTerminate()
    }
    override fun keyOutput(): Observable<KeyEvent> {
        return keySubject.asObservable()
    }

    override fun render(frame: Frame) {
        if(!ready) return
        if (glfwWindowShouldClose(window!!) != GLFW_FALSE) return

        // these calls need to happen again in each call to render to ensure context is initialzed for the thread
        // on which render is invoked.
        GL.setCapabilities(glCapabilities!!)
        glfwMakeContextCurrent(window!!)

        glfwPollEvents()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        frame.actors.forEach {
            // position
            val x = it.x
            val y = it.y
            // sprite
            val sprite = resourceManager.getByteBufferSprite(it.currentSprite())
            if (sprite.comp == 3) {
                if ((sprite.width and 3) != 0)
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (sprite.width and 1))
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, sprite.width, sprite.height, 0, GL_RGB, GL_UNSIGNED_BYTE, sprite.image)
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, sprite.width, sprite.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, sprite.image)
                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            }
            // render sprite
            glPushMatrix()
            glBegin(GL_QUADS)
                glTexCoord2f(0.0f, 0.0f)
                glVertex2f(x, y)

                glTexCoord2f(1.0f, 0.0f)
                glVertex2f(x + sprite.width.toFloat(), y)

                glTexCoord2f(1.0f, 1.0f)
                glVertex2f(x + sprite.width.toFloat(), y + sprite.height.toFloat())

                glTexCoord2f(0.0f, 1.0f)
                glVertex2f(x, y + sprite.height.toFloat())
            glEnd()
            glPopMatrix()
        }

        glfwSwapBuffers(window!!)
    }

    private fun initGL() {
        if (glfwInit() != GLFW_TRUE) throw IllegalStateException("Unable to initialize GLFW")
        glfwSetErrorCallback(errorCallback)

        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        window = glfwCreateWindow(width, height, "RXG", NULL, NULL)
        if (window == NULL) throw RuntimeException("Failed to create the GLFW window")

        glfwSetKeyCallback(window!!, keyCallback)
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        glfwSetWindowPos(window as Long, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2)
        glfwMakeContextCurrent(window!!)
        //glfwSwapInterval(1)
        glfwShowWindow(window!!)
        glCapabilities = GL.createCapabilities()

        glClearColor(0.0f, 1.0f, 0.0f, 0.0f)
        glViewport(0, 0, width, height)
        glMatrixMode(GL_MODELVIEW)
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        glOrtho(0.0, width.toDouble(), height.toDouble(), 0.toDouble(), 1.toDouble(), -1.toDouble())
        glMatrixMode(GL_MODELVIEW)

        val texID = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, texID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glEnable(GL_TEXTURE_2D)

        ready = true
    }
}