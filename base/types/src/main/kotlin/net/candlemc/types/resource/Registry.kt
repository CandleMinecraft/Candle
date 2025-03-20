package net.candlemc.types.resource

import net.candlemc.event.EventBus
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

open class Registry<T>(private val registryIdentifier: Identifier) : NamespacedObject {
    private val registry: MutableMap<Identifier, T> = ConcurrentHashMap<Identifier, T>()
    private val saveFile = File("./.data/@candle/registries", "$registryIdentifier.dat")

    init {
        // Optionally subscribe for debugging.
        eventBus.subscribe(PostRegisterEvent::class.java) { event ->
            val e: PostRegisterEvent<*> = event
            println("[Registry " + registryIdentifier + "] Registered: " + e.id + " -> " + e.entry)
        }
        eventBus.subscribe(PostRemoveEvent::class.java) { event ->
            val e: PostRemoveEvent<*> = event
            println("[Registry " + registryIdentifier + "] Removed: " + e.id + " -> " + e.removedEntry)
        }
        eventBus.subscribe(UpdateEvent::class.java) { event ->
            val e: UpdateEvent<*> = event
            println("[Registry " + registryIdentifier + "] Updated: " + e.id + " -> " + e.updatedEntry)
        }
    }

    // Registers an entry using ResourceLocation as the key.
    fun register(id: Identifier, entry: T): T {
        require(!registry.containsKey(id)) { "ID already registered: $id" }
        val preEvent: PreRegisterEvent<T> = PreRegisterEvent<T>(this, id)
        eventBus.publish(preEvent).thenRun {
            if (preEvent.isCancelled) {
                println("[Registry $registryIdentifier] Registration canceled for: $id")
                return@thenRun
            }
            registry[id] = entry
            val postEvent: PostRegisterEvent<T> = PostRegisterEvent<T>(this, id, entry)
            eventBus.publish(postEvent)
            saveToDisk()
        }
        return entry
    }

    fun register(entry: T): T {
        if (entry is NamespacedObject) {
            return register(entry.namespacedIdentifier(), entry)
        }
        throw IllegalArgumentException("result of supplier is required to have a namespaced identifier!")
    }

    // Removes an entry and fires corresponding events.
    fun remove(id: Identifier) {
        if (!registry.containsKey(id)) {
            println("[Registry $registryIdentifier] No entry found for removal: $id")
            return
        }
        val entry = registry[id]
        val preEvent: PreRemoveEvent<T> = PreRemoveEvent<T>(this, id, entry!!)
        eventBus.publish(preEvent).thenRun {
            if (preEvent.isCancelled) {
                println("[Registry $registryIdentifier] Removal canceled for: $id")
                return@thenRun
            }
            val removed = registry.remove(id)
            val postEvent: PostRemoveEvent<T> = PostRemoveEvent<T>(this, id, removed!!)
            eventBus.publish(postEvent)
            saveToDisk()
        }
    }

    // Updates an existing entry and fires an update event.
    fun update(id: Identifier, supplier: Supplier<out T>) {
        require(registry.containsKey(id)) { "ID not registered: $id" }
        val newEntry = supplier.get()
        registry[id] = newEntry
        val updateEvent: UpdateEvent<T> = UpdateEvent<T>(this, id, newEntry)
        eventBus.publish(updateEvent)
        saveToDisk()
    }

    // Retrieves an entry by its ResourceLocation.
    fun get(id: Identifier): T? {
        return registry[id]
    }

    // Checks if an entry is registered.
    fun isRegistered(id: Identifier): Boolean {
        return registry.containsKey(id)
    }

    val all: Map<Identifier, T>
        // Returns an unmodifiable view of all registered entries.
        get() = Collections.unmodifiableMap(registry)

    // Saves the registry to disk.
    fun saveToDisk() {
        try {
            ObjectOutputStream(FileOutputStream(saveFile)).use { oos ->
                oos.writeObject(HashMap(registry)) // Save a serializable copy.
                println("[Registry $registryIdentifier] Saved successfully.")
            }
        } catch (e: IOException) {
            System.err.println("[Registry " + registryIdentifier + "] Save failed: " + e.message)
        }
    }

    // Loads registry data from disk.
    fun loadFromDisk() {
        if (!saveFile.exists()) {
            println("[Registry $registryIdentifier] No saved data found.")
            return
        }
        try {
            ObjectInputStream(FileInputStream(saveFile)).use { ois ->
                val loadedData = ois.readObject() as Map<Identifier, T>
                registry.clear()
                registry.putAll(loadedData)
                println("[Registry $registryIdentifier] Loaded successfully.")
            }
        } catch (e: IOException) {
            System.err.println("[Registry " + registryIdentifier + "] Load failed: " + e.message)
        } catch (e: ClassNotFoundException) {
            System.err.println("[Registry " + registryIdentifier + "] Load failed: " + e.message)
        }
    }

    override fun namespacedIdentifier(): Identifier {
        return this.registryIdentifier
    }

    companion object {
        private val eventBus: EventBus = EventBus()
        fun getEventBus(): EventBus {
            return eventBus
        }
    }
}
