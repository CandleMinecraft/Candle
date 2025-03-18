package candle.types.primitives;

/**
 * Represents an 8-bit signed integer (byte) with extended arithmetic and utility operations. This class provides a
 * fluent API for common arithmetic operations while ensuring that the underlying value conforms to the 8-bit signed
 * range.
 * <p>
 * <strong>Note:</strong> Overflow handling follows Java's native behavior. Additional checks
 * may be implemented if stricter overflow detection is required.
 * </p>
 */
public class Int8 extends NumericPrimitive<Byte, Int8> {

  /**
   * Constructs a new {@code Int8} instance with the specified byte value.
   *
   * @param value the byte value to encapsulate
   */
  public Int8( byte value ) {
    super(value);
  }

  /**
   * Creates a new {@code Int8} instance with the given byte value.
   *
   * @param value the byte value for the new instance
   *
   * @return a new {@code Int8} instance encapsulating the provided value
   */
  @Override
  protected Int8 newInstance( Byte value ) {
    return new Int8(value);
  }

  /**
   * Converts a {@code double} value to a {@code byte}. The conversion truncates the fractional part and adheres to
   * Java's native narrowing conversion, which may result in overflow.
   *
   * @param d the double value to convert
   *
   * @return the resulting byte value
   */
  @Override
  protected Byte convertFromDouble( double d ) {
    return (byte) d;
  }
}
