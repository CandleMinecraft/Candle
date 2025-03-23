package net.candlemc.mvt


class Color(val hex: String?, val chat: String, val motd: String) {
    companion object {
        val WHITE: Color = Color("#FFFFFF", "§f", "\\u00A7f")
        val BLACK: Color = Color("#000000", "§0", "\\u00A70")
        val DARK_BLUE: Color = Color("#0000AA", "§1", "\\u00A71")
        val DARK_GREEN: Color = Color("#00AA00", "§2", "\\u00A72")
        val DARK_AQUA: Color = Color("#00AAAA", "§3", "\\u00A73")
        val DARK_RED: Color = Color("#AA0000", "§4", "\\u00A74")
        val DARK_PURPLE: Color = Color("#AA00AA", "§5", "\\u00A75")
        val GOLD: Color = Color("#FFAA00", "§6", "\\u00A76")
        val GRAY: Color = Color("#AAAAAA", "§7", "\\u00A77")
        val DARK_GRAY: Color = Color("#555555", "§8", "\\u00A78")
        val BLUE: Color = Color("#5555FF", "§9", "\\u00A79")
        val GREEN: Color = Color("#55FF55", "§a", "\\u00A7a")
        val AQUA: Color = Color("#55FFFF", "§b", "\\u00A7b")
        val RED: Color = Color("#FF5555", "§c", "\\u00A7c")
        val LIGHT_PURPLE: Color = Color("#FF55FF", "§d", "\\u00A7d")
        val YELLOW: Color = Color("#FFFF55", "§e", "\\u00A7e")
        val OBFUSCATED: Color = Color(null, "§k", "\\u00A7k")
        val BOLD: Color = Color(null, "§l", "\\u00A7l")
        val STRIKETHROUGH: Color = Color(null, "§m", "\\u00A7m")
        val UNDERLINE: Color = Color(null, "§n", "\\u00A7n")
        val ITALIC: Color = Color(null, "§o", "\\u00A7o")
        val RESET: Color = Color(null, "§r", "\\u00A7r")
    }

}
