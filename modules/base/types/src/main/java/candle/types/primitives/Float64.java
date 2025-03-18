package candle.types.primitives;

/**
 * Represents a 64-bit floating point number with extended arithmetic and utility operations. This class provides a
 * fluent API for arithmetic operations and additional mathematical functions tailored for 64-bit IEEE 754 floating
 * point numbers.
 * <p>
 * <strong>Note:</strong> The underlying value is of type {@code double} and offers higher precision
 * compared to {@code Float32}.
 * </p>
 */
public class Float64 extends NumericPrimitive<Double, Float64> {

  /**
   * Constructs a new {@code Float64} instance with the specified double value.
   *
   * @param value the double value to encapsulate
   */
  public Float64( double value ) {
    super(value);
  }

  /**
   * Creates a new {@code Float64} instance with the given double value.
   *
   * @param value the double value for the new instance
   *
   * @return a new {@code Float64} instance encapsulating the provided value
   */
  @Override
  protected Float64 newInstance( Double value ) {
    return new Float64(value);
  }

  /**
   * Converts a {@code double} value to a {@code double}.
   *
   * @param d the double value to convert
   *
   * @return the same double value
   */
  @Override
  protected Double convertFromDouble( double d ) {
    return d;
  }

  /**
   * Computes the square root of this double value.
   *
   * @return a new {@code Float64} instance representing the square root of this value
   */
  public Float64 sqrt() {
    return newInstance(Math.sqrt(this.asDouble()));
  }

  /**
   * Computes the sine of this double value.
   *
   * @return a new {@code Float64} instance representing the sine of this value
   */
  public Float64 sin() {
    return newInstance(Math.sin(this.asDouble()));
  }

  /**
   * Computes the cosine of this double value.
   *
   * @return a new {@code Float64} instance representing the cosine of this value
   */
  public Float64 cos() {
    return newInstance(Math.cos(this.asDouble()));
  }

  /**
   * Computes the tangent of this double value.
   *
   * @return a new {@code Float64} instance representing the tangent of this value
   */
  public Float64 tan() {
    return newInstance(Math.tan(this.asDouble()));
  }
}
