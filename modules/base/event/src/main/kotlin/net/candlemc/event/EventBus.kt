package net.candlemc.event

import java.util.concurrent.*
import java.util.function.Consumer

/**
 * The `EventBus` class provides a simple event bus system where listeners can subscribe to events of a specific
 * type, and events can be published asynchronously.
 *
 *
 * All listeners are stored in a static map shared across all instances of `EventBus`. This class utilizes a fixed
 * thread pool [ExecutorService] to dispatch events asynchronously.
 *
 */
class EventBus {
    /**
     * Subscribes a listener for a specific event type.
     *
     *
     * This method registers the provided listener to be notified when an event of the specified type is published. Note
     * that all instances of `EventBus` share the same listener registry.
     *
     *
     * @param <T>       the type of the event
     * @param eventType the `Class` object representing the event type
     * @param listener  the listener to be notified when an event of the given type is published
    </T> */
    fun <T : Event<*>> subscribe(eventType: Class<T>, listener: Consumer<T>) {
        listeners.computeIfAbsent(eventType) { CopyOnWriteArrayList() }.add(listener as Consumer<*>)
    }

    /**
     * Unsubscribes a listener for a specific event type.
     *
     *
     * This method removes the provided listener from the list of consumers that are notified when an event of the
     * specified type is published.
     *
     *
     * @param <T>       the type of the event
     * @param eventType the `Class` object representing the event type
     * @param listener  the listener to be removed
    </T> */
    fun <T : Event<*>> unsubscribe(eventType: Class<T>, listener: Consumer<T>) {
        val eventListeners = listeners[eventType]
        eventListeners?.remove(listener)
    }

    /**
     * Publishes an event asynchronously to all subscribed listeners.
     *
     *
     * This method dispatches the event to all listeners registered for the event's class. The event handling is performed
     * asynchronously using a shared [ExecutorService].
     *
     *
     * @param <T>   the type of the event
     * @param event the event to be published
     *
     * @return a [CompletableFuture] that completes when all listeners have been notified
    </T> */
    fun <T : Event<*>> publish(event: T): CompletableFuture<Void?> {
        val eventListeners = listeners[event::class.java]
            ?: return CompletableFuture.completedFuture<Void?>(null)

        val futures: MutableList<CompletableFuture<Void>> = ArrayList()
        for (listener in eventListeners) {
            val future = CompletableFuture.runAsync(
                { (listener as Consumer<T>).accept(event) },
                executorService
            )
            futures.add(future)
        }
        return CompletableFuture.allOf(*futures.toTypedArray<CompletableFuture<*>>())
    }

    companion object {
        /**
         * Static listener storage shared across all instances. The map key is the event class and the value is a list of
         * consumers registered to handle events of that type.
         */
        private val listeners: MutableMap<Class<*>, MutableList<Consumer<*>>> = ConcurrentHashMap()

        /**
         * Executor service for asynchronous event dispatching.
         */
        private val executorService: ExecutorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

        /**
         * Shuts down the [ExecutorService] used for asynchronous event dispatching.
         *
         *
         * This method should be called only once from a central point when the event bus is no longer needed.
         *
         */
        fun shutdown() {
            executorService.shutdown()
        }
    }
}
