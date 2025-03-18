package candle.logger;

// From https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
public class ANSIColors {
  // Regular Colors
  public static final String BLACK = "\u001B[0;30m";
  public static final String RED = "\u001B[0;31m";
  public static final String GREEN = "\u001B[0;32m";
  public static final String YELLOW = "\u001B[0;33m";
  public static final String BLUE = "\u001B[0;34m";
  public static final String PURPLE = "\u001B[0;35m";
  public static final String CYAN = "\u001B[0;36m";
  public static final String WHITE = "\u001B[0;37m";

  // Bold
  public static final String BOLD_BLACK = "\u001B[1;30m";
  public static final String BOLD_RED = "\u001B[1;31m";
  public static final String BOLD_GREEN = "\u001B[1;32m";
  public static final String BOLD_YELLOW = "\u001B[1;33m";
  public static final String BOLD_BLUE = "\u001B[1;34m";
  public static final String BOLD_PURPLE = "\u001B[1;35m";
  public static final String BOLD_CYAN = "\u001B[1;36m";
  public static final String BOLD_WHITE = "\u001B[1;37m";

  // Underline
  public static final String UNDERLINE_BLACK = "\u001B[4;30m";
  public static final String UNDERLINE_RED = "\u001B[4;31m";
  public static final String UNDERLINE_GREEN = "\u001B[4;32m";
  public static final String UNDERLINE_YELLOW = "\u001B[4;33m";
  public static final String UNDERLINE_BLUE = "\u001B[4;34m";
  public static final String UNDERLINE_PURPLE = "\u001B[4;35m";
  public static final String UNDERLINE_CYAN = "\u001B[4;36m";
  public static final String UNDERLINE_WHITE = "\u001B[4;37m";

  // Background
  public static final String BACKGROUND_BLACK = "\u001B[40m";
  public static final String BACKGROUND_RED = "\u001B[41m";
  public static final String BACKGROUND_GREEN = "\u001B[42m";
  public static final String BACKGROUND_YELLOW = "\u001B[43m";
  public static final String BACKGROUND_BLUE = "\u001B[44m";
  public static final String BACKGROUND_PURPLE = "\u001B[45m";
  public static final String BACKGROUND_CYAN = "\u001B[46m";
  public static final String BACKGROUND_WHITE = "\u001B[47m";

  // High Intensity
  public static final String HIGH_INTENSITY_BLACK = "\u001B[0;90m";
  public static final String HIGH_INTENSITY_RED = "\u001B[0;91m";
  public static final String HIGH_INTENSITY_GREEN = "\u001B[0;92m";
  public static final String HIGH_INTENSITY_YELLOW = "\u001B[0;93m";
  public static final String HIGH_INTENSITY_BLUE = "\u001B[0;94m";
  public static final String HIGH_INTENSITY_PURPLE = "\u001B[0;95m";
  public static final String HIGH_INTENSITY_CYAN = "\u001B[0;96m";
  public static final String HIGH_INTENSITY_WHITE = "\u001B[0;97m";

  // Bold High Intensity
  public static final String BOLD_HIGH_INTENSITY_BLACK = "\u001B[1;90m";
  public static final String BOLD_HIGH_INTENSITY_RED = "\u001B[1;91m";
  public static final String BOLD_HIGH_INTENSITY_GREEN = "\u001B[1;92m";
  public static final String BOLD_HIGH_INTENSITY_YELLOW = "\u001B[1;93m";
  public static final String BOLD_HIGH_INTENSITY_BLUE = "\u001B[1;94m";
  public static final String BOLD_HIGH_INTENSITY_PURPLE = "\u001B[1;95m";
  public static final String BOLD_HIGH_INTENSITY_CYAN = "\u001B[1;96m";
  public static final String BOLD_HIGH_INTENSITY_WHITE = "\u001B[1;97m";

  // High Intensity backgrounds
  public static final String BACKGROUND_HIGH_INTENSITY_BLACK = "\u001B[0;100m";
  public static final String BACKGROUND_HIGH_INTENSITY_RED = "\u001B[0;101m";
  public static final String BACKGROUND_HIGH_INTENSITY_GREEN = "\u001B[0;102m";
  public static final String BACKGROUND_HIGH_INTENSITY_YELLOW = "\u001B[0;103m";
  public static final String BACKGROUND_HIGH_INTENSITY_BLUE = "\u001B[0;104m";
  public static final String BACKGROUND_HIGH_INTENSITY_PURPLE = "\u001B[0;105m";
  public static final String BACKGROUND_HIGH_INTENSITY_CYAN = "\u001B[0;106m";
  public static final String BACKGROUND_HIGH_INTENSITY_WHITE = "\u001B[0;107m";

  // Reset
  public static final String RESET = "\u001B[0m";

  // Text Styles
  public static final String BOLD = "\u001B[1m";
  public static final String ITALIC = "\u001B[3m";
  public static final String UNDERLINE = "\u001B[4m";
  public static final String STRIKETHROUGH = "\u001B[9m";
}