package candle.types.resource;

import candle.event.Event;

public class PostRemoveEvent<T> extends Event<Registry<T>> {
  private final Identifier id;
  private final T removedEntry;

  public PostRemoveEvent( Registry<T> registry, Identifier id, T removedEntry ) {
    super(registry);
    this.id = id;
    this.removedEntry = removedEntry;
  }

  public Identifier getId() {
    return id;
  }

  public T getRemovedEntry() {
    return removedEntry;
  }
}
