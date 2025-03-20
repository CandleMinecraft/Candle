package net.candlemc.types.resource

import net.candlemc.event.Event

class PostRemoveEvent<T>(registry: Registry<T>?, val id: Identifier, val removedEntry: T) :
    Event<Registry<T>?>(registry)
