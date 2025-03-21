package net.candlemc.functions

fun interface TriFunction<T, U, V, R> {
    fun apply(t: T, u: U, v: V): R
}
