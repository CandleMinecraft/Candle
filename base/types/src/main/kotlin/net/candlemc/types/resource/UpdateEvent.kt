package net.candlemc.types.resource

import net.candlemc.event.Event

class UpdateEvent<T>(registry: Registry<T>?, val id: Identifier, val updatedEntry: T) :
    Event<Registry<T>?>(registry)
