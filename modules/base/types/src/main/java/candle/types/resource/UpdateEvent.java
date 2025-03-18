package candle.types.resource;

import candle.event.Event;

public class UpdateEvent<T> extends Event<Registry<T>> {
  private final Identifier id;
  private final T updatedEntry;

  public UpdateEvent( Registry<T> registry, Identifier id, T updatedEntry ) {
    super(registry);
    this.id = id;
    this.updatedEntry = updatedEntry;
  }

  public Identifier getId() {
    return id;
  }

  public T getUpdatedEntry() {
    return updatedEntry;
  }
}
