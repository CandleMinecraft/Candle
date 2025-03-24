package de.terrocraft

import Color


class TextBuilder(private var text: String) {

    fun replace(old: String, new: String): TextBuilder {
        text = text.replace(old, new)
        return this
    }

    fun prefix(prefix: String): TextBuilder {
        text = prefix + text
        return this
    }

    fun suffix(suffix: String): TextBuilder {
        text += suffix
        return this
    }

    fun get(): String {
        return applyColors(text)
    }

    fun colored(): String {
        return applyColors(text).replace("&", "§")
    }

    private fun applyColors(inputText: String): String {
        var modifiedText = inputText
        for (value in Color.values) {
            if (modifiedText.contains(value.toString())) {
                modifiedText = modifiedText.replace(value.toString(), value.chat)
            }
        }
        return modifiedText
    }
}