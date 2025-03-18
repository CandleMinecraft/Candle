package candle.types.primitives;

/**
 * Represents a 16-bit signed integer (short) with extended arithmetic and utility operations. This class provides a
 * fluent API for arithmetic operations while ensuring that the underlying value remains within the 16-bit signed
 * range.
 * <p>
 * <strong>Note:</strong> Overflow handling is based on Java's native behavior.
 * </p>
 */
public class Int16 extends NumericPrimitive<Short, Int16> {

  /**
   * Constructs a new {@code Int16} instance with the specified short value.
   *
   * @param value the short value to encapsulate
   */
  public Int16( short value ) {
    super(value);
  }

  /**
   * Creates a new {@code Int16} instance with the given short value.
   *
   * @param value the short value for the new instance
   *
   * @return a new {@code Int16} instance encapsulating the provided value
   */
  @Override
  protected Int16 newInstance( Short value ) {
    return new Int16(value);
  }

  /**
   * Converts a {@code double} value to a {@code short}. The conversion truncates the fractional component, following
   * Java's native narrowing conversion.
   *
   * @param d the double value to convert
   *
   * @return the resulting short value
   */
  @Override
  protected Short convertFromDouble( double d ) {
    return (short) d;
  }
}
