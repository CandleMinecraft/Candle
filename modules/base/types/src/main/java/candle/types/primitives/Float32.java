package candle.types.primitives;

/**
 * Represents a 32-bit floating point number with extended arithmetic and utility operations. This class provides a
 * fluent API for arithmetic operations and additional mathematical functions tailored for 32-bit IEEE 754 floating
 * point numbers.
 * <p>
 * <strong>Note:</strong> The underlying value is of type {@code float}, and precision loss may occur
 * during arithmetic operations.
 * </p>
 */
public class Float32 extends NumericPrimitive<Float, Float32> {

  /**
   * Constructs a new {@code Float32} instance with the specified float value.
   *
   * @param value the float value to encapsulate
   */
  public Float32( float value ) {
    super(value);
  }

  /**
   * Creates a new {@code Float32} instance with the given float value.
   *
   * @param value the float value for the new instance
   *
   * @return a new {@code Float32} instance encapsulating the provided value
   */
  @Override
  protected Float32 newInstance( Float value ) {
    return new Float32(value);
  }

  /**
   * Converts a {@code double} value to a {@code float}. The conversion may result in a loss of precision.
   *
   * @param d the double value to convert
   *
   * @return the resulting float value
   */
  @Override
  protected Float convertFromDouble( double d ) {
    return (float) d;
  }

  /**
   * Computes the square root of this floating point value.
   *
   * @return a new {@code Float32} instance representing the square root of this value
   */
  public Float32 sqrt() {
    return newInstance((float) Math.sqrt(this.asDouble()));
  }

  /**
   * Computes the sine of this floating point value.
   *
   * @return a new {@code Float32} instance representing the sine of this value
   */
  public Float32 sin() {
    return newInstance((float) Math.sin(this.asDouble()));
  }

  /**
   * Computes the cosine of this floating point value.
   *
   * @return a new {@code Float32} instance representing the cosine of this value
   */
  public Float32 cos() {
    return newInstance((float) Math.cos(this.asDouble()));
  }

  /**
   * Computes the tangent of this floating point value.
   *
   * @return a new {@code Float32} instance representing the tangent of this value
   */
  public Float32 tan() {
    return newInstance((float) Math.tan(this.asDouble()));
  }
}
