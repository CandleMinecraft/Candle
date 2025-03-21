package net.candlemc.types.resource

import net.candlemc.event.CancelableEvent

class PreRemoveEvent<T>(
    registry: Registry<T>, val id: Identifier,
    val entry: T
) :
    CancelableEvent<Registry<T>?>(registry) {
    private val registry: Registry<T> = registry

    fun getRegistry(): Registry<T> {
        return registry
    }
}
