package candle.logger;

public class LoggerBuilder {
  private boolean showDate = false;
  private boolean showTime = true;
  private boolean showLogLevel = true;
  private boolean showClassName = true;
  private boolean showLineNumber = true;

  public LoggerBuilder setShowLineNumber( boolean showLineNumber ) {
    this.showLineNumber = showLineNumber;
    return this;
  }

  public LoggerBuilder setShowClassName( boolean showClassName ) {
    this.showClassName = showClassName;
    return this;
  }

  public LoggerBuilder setShowLogLevel( boolean showLogLevel ) {
    this.showLogLevel = showLogLevel;
    return this;
  }

  public LoggerBuilder setShowTime( boolean showTime ) {
    this.showTime = showTime;
    return this;
  }

  public LoggerBuilder setShowDate( boolean showDate ) {
    this.showDate = showDate;
    return this;
  }

  public Logger build() {
    return new Logger(showDate, showTime, showLogLevel, showClassName, showLineNumber);
  }
}
