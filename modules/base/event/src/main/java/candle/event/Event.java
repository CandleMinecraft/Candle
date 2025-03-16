package candle.event;

public class Event<S> {
  private final S source;
  private final long timestamp;

  protected Event( S source ) {
    this.source = source;
    this.timestamp = System.currentTimeMillis();
  }

  public S getSource() {
    return source;
  }

  public long getTimestamp() {
    return timestamp;
  }
}
