package net.candlemc.event

open class CancelableEvent<S>(source: S) : Event<S>(source) {
    var isCancelled: Boolean = false
}
