package net.candlemc.types.resource

import java.util.regex.Pattern

open class Identifier(namespace: String, path: String) {
    private val namespace: String
    private val path: String

    init {
        require(isValidNamespace(namespace)) { "Invalid namespace: $namespace" }
        require(isValidPath(path)) { "Invalid path: $path" }
        this.namespace = namespace
        this.path = path
    }

    private fun isValidNamespace(ns: String): Boolean {
        return VALID_NAMESPACE.matcher(ns).matches()
    }

    private fun isValidPath(path: String): Boolean {
        return VALID_PATH.matcher(path).matches()
    }

    override fun toString(): String {
        return "$namespace:$path"
    }

    companion object {
        private val VALID_NAMESPACE: Pattern = Pattern.compile("[a-z0-9_.-]+")
        private val VALID_PATH: Pattern = Pattern.compile("[a-z0-9_/.-]+")

        // Parses "namespace:path". Defaults to "minecraft" if no colon is present.
        fun parse(combined: String): Identifier {
            val parts = combined.split(":".toRegex(), limit = 2).toTypedArray()
            if (parts.size == 1) {
                return Identifier("minecraft", parts[0])
            }
            return Identifier(parts[0], parts[1])
        }

        fun of(namespacedIdentifier: String): Identifier {
            val splitIdentifier = namespacedIdentifier.split(":".toRegex(), limit = 1).toTypedArray()
            return Identifier(splitIdentifier[0], splitIdentifier[1])
        }

        fun of(namespace: String, identifier: String): Identifier {
            return Identifier(namespace, identifier)
        }
    }
}
