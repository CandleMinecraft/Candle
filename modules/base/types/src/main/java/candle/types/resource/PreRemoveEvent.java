package candle.types.resource;

import candle.event.CancelableEvent;

public class PreRemoveEvent<T> extends CancelableEvent<Registry<T>> {
  private final Registry<T> registry;
  private final Identifier id;
  private final T entry;

  public PreRemoveEvent( Registry<T> registry, Identifier id, T entry ) {
    super(registry);
    this.registry = registry;
    this.id = id;
    this.entry = entry;
  }

  public Registry<T> getRegistry() {
    return registry;
  }

  public Identifier getId() {
    return id;
  }

  public T getEntry() {
    return entry;
  }
}
