package net.candlemc.protocol

import java.io.Serializable

/**
 * Represents an entry in the lookup cache for packet retrieval.
 *
 * <p>A CacheEntry indicates whether a packet is present (Found) in a specific protocol version or has been explicitly
 * removed (Removed). This information is used by the PacketRegistry to expedite lookup operations by avoiding full traversal
 * of parent registries when the cache is pre-generated.</p>
 */
sealed class CacheEntry : Serializable {
    /**
     * Represents a cache entry where the packet was found in a specific protocol version.
     *
     * @property protocolVersion the protocol version in which the packet is registered.
     */
    data class Found(val protocolVersion: Int) : CacheEntry()

    /**
     * Represents a cache entry indicating that the packet has been removed and should not be returned during lookup.
     */
    object Removed : CacheEntry()
}