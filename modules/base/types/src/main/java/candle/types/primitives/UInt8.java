package candle.types.primitives;

/**
 * Represents an 8-bit unsigned integer. This class encapsulates an unsigned 8-bit value (stored as a {@code short}) and
 * provides extended arithmetic and utility operations via a fluent API. The value is masked to always remain within the
 * range [0, 255].
 */
public class UInt8 extends NumericPrimitive<Short, UInt8> {

  /**
   * Constructs a new {@code UInt8} instance with the specified value. The value is masked to enforce the 8-bit unsigned
   * invariant.
   *
   * @param value the initial value (only the lowest 8 bits are considered)
   */
  public UInt8( short value ) {
    super((short) ( value & 0xFF ));
  }

  /**
   * Creates a new {@code UInt8} instance with the given value. The provided value is masked to ensure it remains within
   * the 8-bit unsigned range.
   *
   * @param value the value for the new instance (only the lowest 8 bits are considered)
   *
   * @return a new {@code UInt8} instance encapsulating the provided value
   */
  @Override
  protected UInt8 newInstance( Short value ) {
    return new UInt8((short) ( value & 0xFF ));
  }

  /**
   * Converts a {@code double} value to a {@code short} representing an unsigned 8-bit integer. The conversion truncates
   * the fractional component and applies a mask to ensure the value fits in 8 bits.
   *
   * @param d the double value to convert
   *
   * @return the resulting short value within the unsigned 8-bit range
   */
  @Override
  protected Short convertFromDouble( double d ) {
    return (short) ( ( (int) d ) & 0xFF );
  }
}
