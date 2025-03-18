package candle.types.primitives;

/**
 * An abstract base class that encapsulates a numeric primitive value and provides a comprehensive suite of arithmetic
 * and utility operations for various numeric types. This class is parameterized with two type parameters:
 * <ul>
 *   <li>{@code T} - the underlying numeric type (e.g. {@link Integer}, {@link Long}, {@link Float}, etc.)</li>
 *   <li>{@code SELF} - the concrete subclass type that extends {@code NumericPrimitive}, enabling a fluent API</li>
 * </ul>
 * <p>
 * This design uses the <em>Curiously Recurring Template Pattern (CRTP)</em> to allow type-safe operations
 * that return instances of the concrete type without explicit casting. It provides standard arithmetic methods
 * (addition, subtraction, multiplication, division, and modulo) as well as utility methods such as absolute
 * value, negation, exponentiation, increment, decrement, and methods for obtaining the double representation,
 * signum, and min/max comparisons.
 * </p>
 *
 * @param <T>    the underlying numeric type
 * @param <SELF> the concrete subclass type extending {@code NumericPrimitive}
 */
public abstract class NumericPrimitive<T extends Number & Comparable<T>, SELF extends NumericPrimitive<T, SELF>>
        implements PrimitiveType<T>,
                   Comparable<NumericPrimitive<T, SELF>> {

  protected final T value;

  /**
   * Constructs a new {@code NumericPrimitive} with the specified numeric value.
   *
   * @param value the numeric value to encapsulate
   */
  protected NumericPrimitive( T value ) {
    this.value = value;
  }

  /**
   * Returns the encapsulated numeric value.
   *
   * @return the underlying numeric value of type {@code T}
   */
  @Override
  public T value() {
    return value;
  }

  /**
   * Converts the encapsulated value to a {@code double}. This is useful for performing arithmetic operations using a
   * standard numeric type.
   *
   * @return the numeric value as a {@code double}
   */
  public double asDouble() {
    return value.doubleValue();
  }

  /**
   * Determines whether the encapsulated numeric value is exactly zero.
   *
   * @return {@code true} if the numeric value is zero; {@code false} otherwise
   */
  public boolean isZero() {
    return asDouble() == 0.0;
  }

  /**
   * Determines whether the encapsulated numeric value is positive.
   *
   * @return {@code true} if the numeric value is greater than zero; {@code false} otherwise
   */
  public boolean isPositive() {
    return asDouble() > 0;
  }

  /**
   * Determines whether the encapsulated numeric value is negative.
   *
   * @return {@code true} if the numeric value is less than zero; {@code false} otherwise
   */
  public boolean isNegative() {
    return asDouble() < 0;
  }

  /**
   * Returns the signum function of this numeric value: -1 if negative, 0 if zero, and 1 if positive.
   *
   * @return -1, 0, or 1 as the signum of the numeric value
   */
  public int signum() {
    return Double.compare(asDouble(), 0.0);
  }

  /**
   * Compares this {@code NumericPrimitive} with the specified one based on their encapsulated values.
   *
   * @param other the other {@code NumericPrimitive} to be compared
   *
   * @return a negative integer, zero, or a positive integer as this object's value is less than, equal to, or greater
   * than the other’s value
   */
  @Override
  public int compareTo( NumericPrimitive<T, SELF> other ) {
    return this.value.compareTo(other.value);
  }

  /**
   * Indicates whether some other object is "equal to" this one. Two {@code NumericPrimitive} objects are considered
   * equal if they are of the same concrete type and their encapsulated values are equal.
   *
   * @param o the reference object with which to compare
   *
   * @return {@code true} if this object is equal to the specified object; {@code false} otherwise
   */
  @Override
  public boolean equals( Object o ) {
      if ( this == o ) {
          return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
          return false;
      }
    NumericPrimitive<?, ?> that = (NumericPrimitive<?, ?>) o;
    return value.equals(that.value);
  }

  /**
   * Returns a hash code value for this object based on its encapsulated numeric value.
   *
   * @return the hash code derived from the encapsulated value
   */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Returns a string representation of this {@code NumericPrimitive} which is the string representation of its
   * encapsulated numeric value.
   *
   * @return a string representation of the numeric value
   */
  @Override
  public String toString() {
    return String.valueOf(value);
  }

  /**
   * Creates a new instance of the concrete subclass with the provided numeric value. This method must be implemented by
   * each concrete subclass to return an instance of type {@code SELF}.
   *
   * @param value the numeric value for the new instance
   *
   * @return a new instance of type {@code SELF} encapsulating the provided value
   */
  protected abstract SELF newInstance( T value );

  /**
   * Converts a {@code double} value into the underlying numeric type {@code T}. This conversion is necessary for
   * arithmetic operations that yield a {@code double} result.
   *
   * @param d the {@code double} value to convert
   *
   * @return the converted value as type {@code T}
   */
  protected abstract T convertFromDouble( double d );

  /**
   * Adds the specified operand to this numeric value.
   *
   * @param other the operand to add
   *
   * @return a new instance representing the sum of this value and the operand
   */
  public SELF add( SELF other ) {
    double result = this.asDouble() + other.asDouble();
    return newInstance(convertFromDouble(result));
  }

  /**
   * Subtracts the specified operand from this numeric value.
   *
   * @param other the operand to subtract
   *
   * @return a new instance representing the difference between this value and the operand
   */
  public SELF subtract( SELF other ) {
    double result = this.asDouble() - other.asDouble();
    return newInstance(convertFromDouble(result));
  }

  /**
   * Multiplies this numeric value by the specified operand.
   *
   * @param other the operand to multiply by
   *
   * @return a new instance representing the product of this value and the operand
   */
  public SELF multiply( SELF other ) {
    double result = this.asDouble() * other.asDouble();
    return newInstance(convertFromDouble(result));
  }

  /**
   * Divides this numeric value by the specified operand.
   *
   * @param other the operand to divide by
   *
   * @return a new instance representing the quotient of this value divided by the operand
   *
   * @throws ArithmeticException if the operand's value is zero
   */
  public SELF divide( SELF other ) {
    if ( other.isZero() ) {
      throw new ArithmeticException("Division by zero");
    }
    double result = this.asDouble() / other.asDouble();
    return newInstance(convertFromDouble(result));
  }

  /**
   * Computes the remainder of dividing this numeric value by the specified operand.
   *
   * @param other the operand for the modulo operation
   *
   * @return a new instance representing the remainder of the division
   *
   * @throws ArithmeticException if the operand's value is zero
   */
  public SELF mod( SELF other ) {
    if ( other.isZero() ) {
      throw new ArithmeticException("Modulo by zero");
    }
    double result = this.asDouble() % other.asDouble();
    return newInstance(convertFromDouble(result));
  }

  /**
   * Returns the absolute value of this numeric value.
   *
   * @return a new instance representing the absolute (non-negative) value
   */
  public SELF abs() {
    double result = Math.abs(this.asDouble());
    return newInstance(convertFromDouble(result));
  }

  /**
   * Returns the negation of this numeric value.
   *
   * @return a new instance representing the negated value
   */
  public SELF negate() {
    return newInstance(convertFromDouble(-this.asDouble()));
  }

  /**
   * Raises this numeric value to the power of the specified exponent.
   *
   * @param exponent the exponent to raise this value to
   *
   * @return a new instance representing the result of the exponentiation
   */
  public SELF pow( double exponent ) {
    double result = Math.pow(this.asDouble(), exponent);
    return newInstance(convertFromDouble(result));
  }

  /**
   * Determines the minimum of this numeric value and the specified operand.
   *
   * @param other the operand to compare with
   *
   * @return this instance if it is less than or equal to the operand; otherwise, the operand
   */
  public SELF min( SELF other ) {
    return this.compareTo(other) <= 0 ? (SELF) this : other;
  }

  /**
   * Determines the maximum of this numeric value and the specified operand.
   *
   * @param other the operand to compare with
   *
   * @return this instance if it is greater than or equal to the operand; otherwise, the operand
   */
  public SELF max( SELF other ) {
    return this.compareTo(other) >= 0 ? (SELF) this : other;
  }

  /**
   * Increments this numeric value by one.
   *
   * @return a new instance representing this value incremented by one
   */
  public SELF increment() {
    return add(newInstance(convertFromDouble(1)));
  }

  /**
   * Decrements this numeric value by one.
   *
   * @return a new instance representing this value decremented by one
   */
  public SELF decrement() {
    return subtract(newInstance(convertFromDouble(1)));
  }
}
