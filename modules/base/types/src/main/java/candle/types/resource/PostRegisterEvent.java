package candle.types.resource;

import candle.event.Event;

public class PostRegisterEvent<T> extends Event<Registry<T>> {
  private final Identifier id;
  private final T entry;

  public PostRegisterEvent( Registry<T> registry, Identifier id, T entry ) {
    super(registry);
    this.id = id;
    this.entry = entry;
  }

  public Identifier getId() {
    return id;
  }

  public T getEntry() {
    return entry;
  }
}
