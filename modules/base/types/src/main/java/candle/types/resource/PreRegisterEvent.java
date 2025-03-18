package candle.types.resource;

import candle.event.CancelableEvent;

public class PreRegisterEvent<T> extends CancelableEvent<Registry<T>> {
  private final Registry<T> registry;
  private final Identifier id;

  public PreRegisterEvent( Registry<T> registry, Identifier id ) {
    super(registry);
    this.registry = registry;
    this.id = id;
  }

  public Registry<T> getRegistry() {
    return registry;
  }

  public Identifier getId() {
    return id;
  }
}
