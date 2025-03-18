package candle.types.primitives;

/**
 * Represents a 32-bit unsigned integer. This class encapsulates an unsigned 32-bit value (stored as a {@code long}) and
 * provides a fluent API for arithmetic and utility operations. The value is masked to ensure it remains within the
 * 32-bit unsigned range.
 */
public class UInt32 extends NumericPrimitive<Long, UInt32> {

  /**
   * Constructs a new {@code UInt32} instance with the specified value. The value is masked to enforce the 32-bit
   * unsigned invariant.
   *
   * @param value the initial long value (only the lowest 32 bits are considered)
   */
  public UInt32( long value ) {
    super(value & 0xFFFFFFFFL);
  }

  /**
   * Creates a new {@code UInt32} instance with the given long value. The provided value is masked to ensure it remains
   * within the 32-bit unsigned range.
   *
   * @param value the long value for the new instance (only the lowest 32 bits are considered)
   *
   * @return a new {@code UInt32} instance encapsulating the provided value
   */
  @Override
  protected UInt32 newInstance( Long value ) {
    return new UInt32(value & 0xFFFFFFFFL);
  }

  /**
   * Converts a {@code double} value to a {@code long} representing a 32-bit unsigned integer. The conversion truncates
   * the fractional part and applies a mask.
   *
   * @param d the double value to convert
   *
   * @return the resulting long value within the 32-bit unsigned range
   */
  @Override
  protected Long convertFromDouble( double d ) {
    return ( (long) d ) & 0xFFFFFFFFL;
  }
}
