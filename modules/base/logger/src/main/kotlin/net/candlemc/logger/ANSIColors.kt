package net.candlemc.logger

// From https://gist.github.com/JBlond/2fea43a3049b38287e5e9cefc87b2124
object ANSIColors {
    // Regular Colors
    const val BLACK: String = "\u001B[0;30m"
    const val RED: String = "\u001B[0;31m"
    const val GREEN: String = "\u001B[0;32m"
    const val YELLOW: String = "\u001B[0;33m"
    const val BLUE: String = "\u001B[0;34m"
    const val PURPLE: String = "\u001B[0;35m"
    const val CYAN: String = "\u001B[0;36m"
    const val WHITE: String = "\u001B[0;37m"

    // Bold
    const val BOLD_BLACK: String = "\u001B[1;30m"
    const val BOLD_RED: String = "\u001B[1;31m"
    const val BOLD_GREEN: String = "\u001B[1;32m"
    const val BOLD_YELLOW: String = "\u001B[1;33m"
    const val BOLD_BLUE: String = "\u001B[1;34m"
    const val BOLD_PURPLE: String = "\u001B[1;35m"
    const val BOLD_CYAN: String = "\u001B[1;36m"
    const val BOLD_WHITE: String = "\u001B[1;37m"

    // Underline
    const val UNDERLINE_BLACK: String = "\u001B[4;30m"
    const val UNDERLINE_RED: String = "\u001B[4;31m"
    const val UNDERLINE_GREEN: String = "\u001B[4;32m"
    const val UNDERLINE_YELLOW: String = "\u001B[4;33m"
    const val UNDERLINE_BLUE: String = "\u001B[4;34m"
    const val UNDERLINE_PURPLE: String = "\u001B[4;35m"
    const val UNDERLINE_CYAN: String = "\u001B[4;36m"
    const val UNDERLINE_WHITE: String = "\u001B[4;37m"

    // Background
    const val BACKGROUND_BLACK: String = "\u001B[40m"
    const val BACKGROUND_RED: String = "\u001B[41m"
    const val BACKGROUND_GREEN: String = "\u001B[42m"
    const val BACKGROUND_YELLOW: String = "\u001B[43m"
    const val BACKGROUND_BLUE: String = "\u001B[44m"
    const val BACKGROUND_PURPLE: String = "\u001B[45m"
    const val BACKGROUND_CYAN: String = "\u001B[46m"
    const val BACKGROUND_WHITE: String = "\u001B[47m"

    // High Intensity
    const val HIGH_INTENSITY_BLACK: String = "\u001B[0;90m"
    const val HIGH_INTENSITY_RED: String = "\u001B[0;91m"
    const val HIGH_INTENSITY_GREEN: String = "\u001B[0;92m"
    const val HIGH_INTENSITY_YELLOW: String = "\u001B[0;93m"
    const val HIGH_INTENSITY_BLUE: String = "\u001B[0;94m"
    const val HIGH_INTENSITY_PURPLE: String = "\u001B[0;95m"
    const val HIGH_INTENSITY_CYAN: String = "\u001B[0;96m"
    const val HIGH_INTENSITY_WHITE: String = "\u001B[0;97m"

    // Bold High Intensity
    const val BOLD_HIGH_INTENSITY_BLACK: String = "\u001B[1;90m"
    const val BOLD_HIGH_INTENSITY_RED: String = "\u001B[1;91m"
    const val BOLD_HIGH_INTENSITY_GREEN: String = "\u001B[1;92m"
    const val BOLD_HIGH_INTENSITY_YELLOW: String = "\u001B[1;93m"
    const val BOLD_HIGH_INTENSITY_BLUE: String = "\u001B[1;94m"
    const val BOLD_HIGH_INTENSITY_PURPLE: String = "\u001B[1;95m"
    const val BOLD_HIGH_INTENSITY_CYAN: String = "\u001B[1;96m"
    const val BOLD_HIGH_INTENSITY_WHITE: String = "\u001B[1;97m"

    // High Intensity backgrounds
    const val BACKGROUND_HIGH_INTENSITY_BLACK: String = "\u001B[0;100m"
    const val BACKGROUND_HIGH_INTENSITY_RED: String = "\u001B[0;101m"
    const val BACKGROUND_HIGH_INTENSITY_GREEN: String = "\u001B[0;102m"
    const val BACKGROUND_HIGH_INTENSITY_YELLOW: String = "\u001B[0;103m"
    const val BACKGROUND_HIGH_INTENSITY_BLUE: String = "\u001B[0;104m"
    const val BACKGROUND_HIGH_INTENSITY_PURPLE: String = "\u001B[0;105m"
    const val BACKGROUND_HIGH_INTENSITY_CYAN: String = "\u001B[0;106m"
    const val BACKGROUND_HIGH_INTENSITY_WHITE: String = "\u001B[0;107m"

    // Reset
    const val RESET: String = "\u001B[0m"

    // Text Styles
    const val BOLD: String = "\u001B[1m"
    const val ITALIC: String = "\u001B[3m"
    const val UNDERLINE: String = "\u001B[4m"
    const val STRIKETHROUGH: String = "\u001B[9m"
}