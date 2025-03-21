package net.candlemc.types.resource

import net.candlemc.event.Event

class PostRegisterEvent<T>(registry: Registry<T>?, val id: Identifier, val entry: T) :
    Event<Registry<T>?>(registry)
