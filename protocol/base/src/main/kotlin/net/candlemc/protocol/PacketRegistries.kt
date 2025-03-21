package net.candlemc.protocol

import net.candlemc.types.resource.Identifier
import net.candlemc.types.resource.Registry

/**
 * A registry for managing PacketRegistry instances.
 *
 * <p>This registry maintains a mapping of protocol versions to their corresponding PacketRegistry instances.
 * The registry is identified by the identifier <code>candlemc:packet_registry</code>. Each PacketRegistry within
 * this registry is identified by <code>candlemc:packet_registry/&lt;version&gt;</code>, where <code>&lt;version&gt;</code>
 * represents the protocol version associated with that PacketRegistry.</p>
 *
 * @constructor Creates a PacketRegistry for packet registries with the identifier <code>candlemc:packet_registry</code>.
 */
object PacketRegistries : Registry<PacketRegistry>(Identifier.of("candlemc", "packet_registry")) {

    /**
     * Retrieves the PacketRegistry corresponding to the specified protocol version.
     *
     * <p>This method constructs an identifier using the format <code>candlemc:packet_registry/&lt;protocolVersion&gt;</code>
     * and returns the associated PacketRegistry if it exists in the registry, or {@code null} otherwise.</p>
     *
     * @param protocolVersion the protocol version for which the PacketRegistry is requested.
     * @return the PacketRegistry for the given protocol version, or {@code null} if not found.
     */
    fun getRegistryByProtocol(protocolVersion: Int): PacketRegistry? {
        return this.get(Identifier.of("candlemc", "packet_registry/$protocolVersion"))
    }
}
