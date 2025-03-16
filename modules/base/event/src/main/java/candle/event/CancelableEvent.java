package candle.event;

public class CancelableEvent<S> extends Event<S> {
  private boolean canceled = false;

  public CancelableEvent(S source) {
    super(source);
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void cancel() {
    canceled = true;
  }
}
