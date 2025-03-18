package candle.types.primitives;

/**
 * Represents a 32-bit unsigned floating point number with extended arithmetic and utility operations. This class
 * provides a fluent API for arithmetic operations while ensuring that the underlying value is always non-negative by
 * clamping negative inputs to zero.
 * <p>
 * <strong>Note:</strong> The unsigned behavior is simulated; the value is enforced to be at least zero.
 * </p>
 */
public class UFloat32 extends NumericPrimitive<Float, UFloat32> {

  /**
   * Constructs a new {@code UFloat32} instance with the specified float value. Negative values are clamped to zero.
   *
   * @param value the float value to encapsulate; if negative, it is set to zero
   */
  public UFloat32( float value ) {
    super(Math.max(0, value));
  }

  /**
   * Creates a new {@code UFloat32} instance with the given float value. Negative values are clamped to zero to maintain
   * the unsigned invariant.
   *
   * @param value the float value for the new instance; if negative, it is set to zero
   *
   * @return a new {@code UFloat32} instance encapsulating the provided value
   */
  @Override
  protected UFloat32 newInstance( Float value ) {
    return new UFloat32(Math.max(0, value));
  }

  /**
   * Converts a {@code double} value to a {@code float} ensuring non-negativity. If the result is negative, it is
   * clamped to zero.
   *
   * @param d the double value to convert
   *
   * @return the resulting non-negative float value
   */
  @Override
  protected Float convertFromDouble( double d ) {
    return (float) Math.max(0, d);
  }

  /**
   * Computes the square root of this value.
   *
   * @return a new {@code UFloat32} instance representing the square root, ensuring a non-negative result
   */
  public UFloat32 sqrt() {
    return newInstance((float) Math.sqrt(this.asDouble()));
  }

  /**
   * Computes the sine of this value.
   *
   * @return a new {@code UFloat32} instance representing the sine of this value
   */
  public UFloat32 sin() {
    return newInstance((float) Math.sin(this.asDouble()));
  }

  /**
   * Computes the cosine of this value.
   *
   * @return a new {@code UFloat32} instance representing the cosine of this value
   */
  public UFloat32 cos() {
    return newInstance((float) Math.cos(this.asDouble()));
  }

  /**
   * Computes the tangent of this value.
   *
   * @return a new {@code UFloat32} instance representing the tangent of this value
   */
  public UFloat32 tan() {
    return newInstance((float) Math.tan(this.asDouble()));
  }
}
