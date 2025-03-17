package candle.logger;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
  private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
  private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final ZoneId ZONE = ZoneId.systemDefault();

  public static String getCurrentDate() {
    return LocalDateTime.now(ZONE).format(DATE_FMT);
  }

  public static String formatTimestamp(long epochMillis) {
    return TIME_FMT.format(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(epochMillis), ZONE));
  }
}
