package rxg.logger

fun createLogger(init: Logger.() -> Unit) : Logger {
    val logger = Logger()
    logger.init()
    return logger
}

fun <T> consoleLogger(c:Class<T>) : Logger {
    return createLogger {
        label { c.name }
        printFunc {
            { message -> println(message) }
        }
    }
}