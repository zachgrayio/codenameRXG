package rxg.logger

class Logger() {
    private val messageFormat = "[RXG | %s][%s]: %s"

    /**
     * Label initializer
     */
    private var label:String = ""
    fun label(labelClosure:() -> String) {
        label = labelClosure()
    }

    /**
     * Print function
     */
    var printFunc:(message:Any?)->Unit = {}
    fun printFunc(printFuncClosure:() -> (message:Any?)->Unit) {
        printFunc = printFuncClosure()
    }

    /**
     * Log a tag and a message
     */
    fun log(tag:String, msg:String) {
        printFunc(messageFormat.format(label, tag, msg))
    }
}
