package net.candlemc.event

open class Event<S> protected constructor(val source: S) {
    val timestamp: Long = System.currentTimeMillis()
}
