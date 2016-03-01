package rxg.common

interface Factory<T> {
    fun create() : T
}

infix fun String.isRotation(other: String): Boolean {
    return this.plus(this).contains(other)
}