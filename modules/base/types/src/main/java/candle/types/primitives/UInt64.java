package candle.types.primitives;

/**
 * Represents a 64-bit unsigned integer. This class encapsulates a 64-bit unsigned value (stored as a {@code long}) and
 * provides a fluent API for arithmetic and utility operations.
 * <p>
 * <strong>Note:</strong> Since Java does not have a native unsigned 64-bit primitive type, this class uses a
 * {@code long} to represent the value without applying any masking.
 * </p>
 */
public class UInt64 extends NumericPrimitive<Long, UInt64> {

  /**
   * Constructs a new {@code UInt64} instance with the specified value.
   *
   * @param value the long value to encapsulate
   */
  public UInt64( long value ) {
    super(value);
  }

  /**
   * Creates a new {@code UInt64} instance with the given long value.
   *
   * @param value the long value for the new instance
   *
   * @return a new {@code UInt64} instance encapsulating the provided value
   */
  @Override
  protected UInt64 newInstance( Long value ) {
    return new UInt64(value);
  }

  /**
   * Converts a {@code double} value to a {@code long}. The conversion truncates the fractional part.
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
