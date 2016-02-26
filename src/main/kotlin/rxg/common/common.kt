package rxg.common

interface Factory<T> {
    fun create() : T
}
