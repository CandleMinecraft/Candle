package candle.types.primitives;

/**
 * Represents a 64-bit signed integer with extended arithmetic and utility operations. This class provides a fluent API
 * for arithmetic operations, ensuring that the encapsulated value adheres to the 64-bit signed integer constraints.
 * <p>
 * <strong>Note:</strong> Overflow behavior conforms to Java's inherent long arithmetic.
 * </p>
 */
public class Int64 extends NumericPrimitive<Long, Int64> {

  /**
   * Constructs a new {@code Int64} instance with the specified long value.
   *
   * @param value the long value to encapsulate
   */
  public Int64( long value ) {
    super(value);
  }

  /**
   * Creates a new {@code Int64} instance with the given long value.
   *
   * @param value the long value for the new instance
   *
   * @return a new {@code Int64} instance encapsulating the provided value
   */
  @Override
  protected Int64 newInstance( Long value ) {
    return new Int64(value);
  }

  /**
   * Converts a {@code double} value to a {@code long}. The conversion truncates the fractional part, in accordance with
   * Java's standard narrowing conversion.
   *
   * @param d the double value to convert
   *
   * @return the resulting long value
   */
  @Override
  protected Long convertFromDouble( double d ) {
    return (long) d;
  }
}
