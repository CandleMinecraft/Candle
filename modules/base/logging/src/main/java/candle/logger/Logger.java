package candle.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Logger {
  // Get Current Class Name & Line

  private final File file;

  public Logger() {
    this.file = new File(String.format("%s/logs/%s.log", System.getProperty("user.dir"), getCurrentDate()));
  }

  public void info(String message) {}
  public void warn(String message) {}
  public void error(String message) {}
  public void fatal(String message) {}

  private void writeToFile(String className, String lines, String message) throws IOException {
    // Example: 23:01:00 - Hello.class:12-16 - message
    String logEntry = String.format("%s - %s:%s - %s", getCurrentTime(), className, lines, message);

    BufferedWriter writer = Files.newBufferedWriter(
            file.toPath(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
      writer.write(logEntry);
      writer.newLine();
      writer.close();
  }

  private String getCurrentTime() {
    long epochInMillisecond = Instant.now().toEpochMilli();
    LocalDateTime localDateTime = Instant.ofEpochMilli(epochInMillisecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withLocale(Locale.getDefault());
    return localDateTime.format(formatter);
  }

  private String getCurrentDate() {
    long epochInMillisecond = Instant.now().toEpochMilli();
    LocalDateTime localDateTime = Instant.ofEpochMilli(epochInMillisecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.getDefault());
    return localDateTime.format(formatter);
  }
}
