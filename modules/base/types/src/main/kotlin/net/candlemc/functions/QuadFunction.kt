package net.candlemc.functions

fun interface QuadFunction<T, U, V, W, R> {
    fun apply(t: T, u: U, v: V, w: W): R
}
