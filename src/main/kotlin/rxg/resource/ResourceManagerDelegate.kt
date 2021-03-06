package rxg.resource

import kotlin.reflect.KProperty

class ResourceManagerDelegate {
    companion object {
        val resourceManager: ResourceManager = ResourceManagerImpl()
    }
    operator fun getValue(thisRef: Any?, property: KProperty<*>): ResourceManager {
        return resourceManager
    }
}