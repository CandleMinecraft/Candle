package candle.types.primitives;

/**
 * Represents a 16-bit unsigned integer. This class encapsulates an unsigned 16-bit value (stored as an {@code int}) and
 * provides a fluent API for arithmetic and utility operations. The value is masked to always remain within the range
 * [0, 65535].
 */
public class UInt16 extends NumericPrimitive<Integer, UInt16> {

  /**
   * Constructs a new {@code UInt16} instance with the specified value. The value is masked to enforce the 16-bit
   * unsigned range.
   *
   * @param value the initial integer value (only the lowest 16 bits are considered)
   */
  public UInt16( int value ) {
    super(value & 0xFFFF);
  }

  /**
   * Creates a new {@code UInt16} instance with the given integer value. The provided value is masked to ensure it
   * remains within the 16-bit unsigned range.
   *
   * @param value the integer value for the new instance (only the lowest 16 bits are considered)
   *
   * @return a new {@code UInt16} instance encapsulating the provided value
   */
  @Override
  protected UInt16 newInstance( Integer value ) {
    return new UInt16(value & 0xFFFF);
  }

  /**
   * Converts a {@code double} value to an {@code int} representing a 16-bit unsigned integer. The conversion truncates
   * the fractional part and applies a mask to ensure the result is within range.
   *
   * @param d the double value to convert
   *
   * @return the resulting integer value within the 16-bit unsigned range
   */
  @Override
  protected Integer convertFromDouble( double d ) {
    return ( (int) d ) & 0xFFFF;
  }
}
