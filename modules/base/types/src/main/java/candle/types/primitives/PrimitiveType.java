package candle.types.primitives;

/**
 * Represents a primitive type wrapper that encapsulates a value of type {@code T}.
 * <p>
 * This interface defines a single method to retrieve the encapsulated value, providing a common contract for all
 * primitive type wrappers. Implementing classes can extend this interface to include additional functionality such as
 * arithmetic operations, validation, or formatting while maintaining a consistent API.
 * </p>
 *
 * @param <T> the type of the primitive value encapsulated by this wrapper
 */
@FunctionalInterface
public interface PrimitiveType<T> {
  /**
   * Returns the encapsulated value.
   *
   * @return the value of type {@code T}
   */
  T value();
}
