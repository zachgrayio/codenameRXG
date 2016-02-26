package rxg.renderer

import rx.Observable
import rxg.frame.Frame
import rxg.input.KeyEvent
import rxg.logger.consoleLogger
import rxg.resource.ResourceManagerDelegate
import java.awt.Canvas
import java.awt.Component
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

@Deprecated("This engine isn't actively maintained")
abstract class AWTIOEngine(override val width:Int, override val height:Int) : IOEngine {
    override val resourceManager by ResourceManagerDelegate()
    private val logger = consoleLogger(javaClass)
    private var canvas: Canvas = Canvas()

    init {
        logger.log("init", "Initializing components.")
        configure(JFrame())
            .add(
                configure(JPanel())
                    .add(
                        configure(canvas)
                    )
            )
    }

    override fun render(frame: Frame) {
        frame.actors.forEach { actor ->
            val sprite = resourceManager.getBufferedImageSprite(actor.currentSprite())
            canvas.graphics.clearRect(0,0, width, height)
            canvas.graphics.drawImage(sprite, actor.x.toInt(), actor.y.toInt(), sprite.width, sprite.height, null)
        }
    }

    override fun close() {

    }

    override fun keyOutput(): Observable<KeyEvent> {
        return Observable.never()
    }

    private fun <T : Component> configure(component: T):T {
        if (component.javaClass == JFrame::class.java) {
            (component as JFrame).defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
        component.size = Dimension(width, height)
        component.isVisible = true
        return component
    }
}