package candle.types.primitives;

/**
 * Represents a 64-bit unsigned floating point number with extended arithmetic and utility operations. This class
 * provides a fluent API for arithmetic operations while ensuring that the underlying value is always non-negative by
 * clamping negative inputs to zero.
 * <p>
 * <strong>Note:</strong> The unsigned behavior is simulated; negative values are replaced with zero.
 * </p>
 */
public class UFloat64 extends NumericPrimitive<Double, UFloat64> {

  /**
   * Constructs a new {@code UFloat64} instance with the specified double value. Negative values are clamped to zero.
   *
   * @param value the double value to encapsulate; if negative, it is set to zero
   */
  public UFloat64( double value ) {
    super(Math.max(0, value));
  }

  /**
   * Creates a new {@code UFloat64} instance with the given double value. Negative values are clamped to zero to
   * maintain the unsigned invariant.
   *
   * @param value the double value for the new instance; if negative, it is set to zero
   *
   * @return a new {@code UFloat64} instance encapsulating the provided value
   */
  @Override
  protected UFloat64 newInstance( Double value ) {
    return new UFloat64(Math.max(0, value));
  }

  /**
   * Converts a {@code double} value to a {@code double}, ensuring the result is non-negative. Negative results are
   * clamped to zero.
   *
   * @param d the double value to convert
   *
   * @return the resulting non-negative double value
   */
  @Override
  protected Double convertFromDouble( double d ) {
    return Math.max(0, d);
  }

  /**
   * Computes the square root of this value.
   *
   * @return a new {@code UFloat64} instance representing the square root, ensuring a non-negative result
   */
  public UFloat64 sqrt() {
    return newInstance(Math.sqrt(this.asDouble()));
  }

  /**
   * Computes the sine of this value.
   *
   * @return a new {@code UFloat64} instance representing the sine of this value
   */
  public UFloat64 sin() {
    return newInstance(Math.sin(this.asDouble()));
  }

  /**
   * Computes the cosine of this value.
   *
   * @return a new {@code UFloat64} instance representing the cosine of this value
   */
  public UFloat64 cos() {
    return newInstance(Math.cos(this.asDouble()));
  }

  /**
   * Computes the tangent of this value.
   *
   * @return a new {@code UFloat64} instance representing the tangent of this value
   */
  public UFloat64 tan() {
    return newInstance(Math.tan(this.asDouble()));
  }
}
