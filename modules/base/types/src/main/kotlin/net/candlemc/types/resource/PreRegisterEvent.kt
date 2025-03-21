package net.candlemc.types.resource

import net.candlemc.event.CancelableEvent

class PreRegisterEvent<T>(registry: Registry<T>, val id: Identifier) :
    CancelableEvent<Registry<T>?>(registry) {
    private val registry: Registry<T> = registry

    fun getRegistry(): Registry<T> {
        return registry
    }
}
