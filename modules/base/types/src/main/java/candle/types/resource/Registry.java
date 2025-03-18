package candle.types.resource;

import candle.event.EventBus;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Registry<T> implements NamespacedObject {
  private static final EventBus eventBus = new EventBus();
  private final Map<Identifier, T> registry = new ConcurrentHashMap<>();
  private final Identifier registryIdentifier;
  private final File saveFile;

  public Registry( Identifier registryName ) {
    this.registryIdentifier = registryName;
    this.saveFile = new File("./.data/@candle/registries", registryName + ".dat");

    // Optionally subscribe for debugging.
    eventBus.subscribe(PostRegisterEvent.class, event -> {
      PostRegisterEvent<?> e = (PostRegisterEvent<?>) event;
      System.out.println("[Registry " + registryName + "] Registered: " + e.getId() + " -> " + e.getEntry());
    });
    eventBus.subscribe(PostRemoveEvent.class, event -> {
      PostRemoveEvent<?> e = (PostRemoveEvent<?>) event;
      System.out.println("[Registry " + registryName + "] Removed: " + e.getId() + " -> " + e.getRemovedEntry());
    });
    eventBus.subscribe(UpdateEvent.class, event -> {
      UpdateEvent<?> e = (UpdateEvent<?>) event;
      System.out.println("[Registry " + registryName + "] Updated: " + e.getId() + " -> " + e.getUpdatedEntry());
    });
  }

  public static EventBus getEventBus() {
    return eventBus;
  }

  // Registers an entry using ResourceLocation as the key.
  public T register( Identifier id, T entry ) {
    if ( registry.containsKey(id) ) {
      throw new IllegalArgumentException("ID already registered: " + id);
    }
    PreRegisterEvent<T> preEvent = new PreRegisterEvent<>(this, id);
    eventBus.publish(preEvent).thenRun(() -> {
      if ( preEvent.isCanceled() ) {
        System.out.println("[Registry " + registryIdentifier + "] Registration canceled for: " + id);
        return;
      }
      registry.put(id, entry);
      PostRegisterEvent<T> postEvent = new PostRegisterEvent<>(this, id, entry);
      eventBus.publish(postEvent);
      saveToDisk();
    });
    return entry;
  }

  public T register( T entry ) {
    if ( entry instanceof NamespacedObject namespacedEntry ) {
      return register(namespacedEntry.namespacedIdentifier(), entry);
    }
    throw new IllegalArgumentException("result of supplier is required to have a namespaced identifier!");
  }

  // Removes an entry and fires corresponding events.
  public void remove( Identifier id ) {
    if ( !registry.containsKey(id) ) {
      System.out.println("[Registry " + registryIdentifier + "] No entry found for removal: " + id);
      return;
    }
    T entry = registry.get(id);
    PreRemoveEvent<T> preEvent = new PreRemoveEvent<>(this, id, entry);
    eventBus.publish(preEvent).thenRun(() -> {
      if ( preEvent.isCanceled() ) {
        System.out.println("[Registry " + registryIdentifier + "] Removal canceled for: " + id);
        return;
      }
      T removed = registry.remove(id);
      PostRemoveEvent<T> postEvent = new PostRemoveEvent<>(this, id, removed);
      eventBus.publish(postEvent);
      saveToDisk();
    });
  }

  // Updates an existing entry and fires an update event.
  public void update( Identifier id, Supplier<? extends T> supplier ) {
    if ( !registry.containsKey(id) ) {
      throw new IllegalArgumentException("ID not registered: " + id);
    }
    T newEntry = supplier.get();
    registry.put(id, newEntry);
    UpdateEvent<T> updateEvent = new UpdateEvent<>(this, id, newEntry);
    eventBus.publish(updateEvent);
    saveToDisk();
  }

  // Retrieves an entry by its ResourceLocation.
  public T get( Identifier id ) {
    return registry.get(id);
  }

  // Checks if an entry is registered.
  public boolean isRegistered( Identifier id ) {
    return registry.containsKey(id);
  }

  // Returns an unmodifiable view of all registered entries.
  public Map<Identifier, T> getAll() {
    return Collections.unmodifiableMap(registry);
  }

  // Saves the registry to disk.
  public void saveToDisk() {
    try ( ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile)) ) {
      oos.writeObject(new HashMap<>(registry)); // Save a serializable copy.
      System.out.println("[Registry " + registryIdentifier + "] Saved successfully.");
    } catch ( IOException e ) {
      System.err.println("[Registry " + registryIdentifier + "] Save failed: " + e.getMessage());
    }
  }

  // Loads registry data from disk.
  @SuppressWarnings("unchecked")
  public void loadFromDisk() {
    if ( !saveFile.exists() ) {
      System.out.println("[Registry " + registryIdentifier + "] No saved data found.");
      return;
    }
    try ( ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile)) ) {
      Map<Identifier, T> loadedData = (Map<Identifier, T>) ois.readObject();
      registry.clear();
      registry.putAll(loadedData);
      System.out.println("[Registry " + registryIdentifier + "] Loaded successfully.");
    } catch ( IOException | ClassNotFoundException e ) {
      System.err.println("[Registry " + registryIdentifier + "] Load failed: " + e.getMessage());
    }
  }

  @Override
  public Identifier namespacedIdentifier() {
    return this.registryIdentifier;
  }
}
