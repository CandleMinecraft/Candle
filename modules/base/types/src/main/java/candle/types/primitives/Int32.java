package candle.types.primitives;

/**
 * Represents a 32-bit signed integer with extended arithmetic and utility operations. This class provides a fluent API
 * for arithmetic operations while ensuring that the value remains within the 32-bit signed integer range.
 * <p>
 * <strong>Note:</strong> Overflow is handled according to Java's inherent integer arithmetic.
 * </p>
 */
public class Int32 extends NumericPrimitive<Integer, Int32> {

  /**
   * Constructs a new {@code Int32} instance with the specified integer value.
   *
   * @param value the integer value to encapsulate
   */
  public Int32( int value ) {
    super(value);
  }

  /**
   * Creates a new {@code Int32} instance with the given integer value.
   *
   * @param value the integer value for the new instance
   *
   * @return a new {@code Int32} instance encapsulating the provided value
   */
  @Override
  protected Int32 newInstance( Integer value ) {
    return new Int32(value);
  }

  /**
   * Converts a {@code double} value to an {@code int}. The conversion truncates the fractional part, following Java's
   * standard narrowing conversion.
   *
   * @param d the double value to convert
   *
   * @return the resulting integer value
   */
  @Override
  protected Integer convertFromDouble( double d ) {
    return (int) d;
  }
}
