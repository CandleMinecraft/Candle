package candle.logger;

import lombok.Getter;

@Getter
public enum LogLevel {
  INFO("\u001B[34mINFO\u001B[0m"),    // Blue
  WARN("\u001B[33mWARN\u001B[0m"),    // Yellow
  ERROR("\u001B[31mERROR\u001B[0m"),   // Red
  FATAL("\u001B[31;1mFATAL\u001B[0m");// Bold Red

  private final String coloredLevel;
  LogLevel(String coloredLevel) { this.coloredLevel = coloredLevel; }
}
