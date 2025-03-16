package candle.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * The {@code EventBus} class provides a simple event bus system where listeners can subscribe to events of a specific
 * type, and events can be published asynchronously.
 * <p>
 * All listeners are stored in a static map shared across all instances of {@code EventBus}. This class utilizes a fixed
 * thread pool {@link ExecutorService} to dispatch events asynchronously.
 * </p>
 */
public class EventBus {

  /**
   * Static listener storage shared across all instances. The map key is the event class and the value is a list of
   * consumers registered to handle events of that type.
   */
  private static final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

  /**
   * Executor service for asynchronous event dispatching.
   */
  private static final ExecutorService executorService =
          Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  /**
   * Shuts down the {@link ExecutorService} used for asynchronous event dispatching.
   * <p>
   * This method should be called only once from a central point when the event bus is no longer needed.
   * </p>
   */
  public static void shutdown() {
    executorService.shutdown();
  }

  /**
   * Subscribes a listener for a specific event type.
   * <p>
   * This method registers the provided listener to be notified when an event of the specified type is published. Note
   * that all instances of {@code EventBus} share the same listener registry.
   * </p>
   *
   * @param <T>       the type of the event
   * @param eventType the {@code Class} object representing the event type
   * @param listener  the listener to be notified when an event of the given type is published
   */
  public <T extends Event> void subscribe( Class<T> eventType, Consumer<T> listener ) {
    listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
  }

  /**
   * Unsubscribes a listener for a specific event type.
   * <p>
   * This method removes the provided listener from the list of consumers that are notified when an event of the
   * specified type is published.
   * </p>
   *
   * @param <T>       the type of the event
   * @param eventType the {@code Class} object representing the event type
   * @param listener  the listener to be removed
   */
  public <T extends Event> void unsubscribe( Class<T> eventType, Consumer<T> listener ) {
    List<Consumer<?>> eventListeners = listeners.get(eventType);
    if ( eventListeners != null ) {
      eventListeners.remove(listener);
    }
  }

  /**
   * Publishes an event asynchronously to all subscribed listeners.
   * <p>
   * This method dispatches the event to all listeners registered for the event's class. The event handling is performed
   * asynchronously using a shared {@link ExecutorService}.
   * </p>
   *
   * @param <T>   the type of the event
   * @param event the event to be published
   *
   * @return a {@link CompletableFuture} that completes when all listeners have been notified
   */
  @SuppressWarnings("unchecked")
  public <T extends Event> CompletableFuture<Void> publish( T event ) {
    List<Consumer<?>> eventListeners = listeners.get(event.getClass());
    if ( eventListeners == null ) {
      return CompletableFuture.completedFuture(null);
    }

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for ( Consumer<?> listener : eventListeners ) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(
              () -> ( (Consumer<T>) listener ).accept(event),
              executorService
                                                                 );
      futures.add(future);
    }
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }
}
