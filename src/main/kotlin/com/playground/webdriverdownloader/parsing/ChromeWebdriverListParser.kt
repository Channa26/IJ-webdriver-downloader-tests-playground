package com.playground.webdriverdownloader.parsing

import com.intellij.openapi.diagnostic.Logger
import com.playground.webdriverdownloader.Architecture
import com.playground.webdriverdownloader.Browser
import com.playground.webdriverdownloader.WebdriverVersion
import org.jsoup.Jsoup

class ChromeWebdriverListParser : WebdriverListParser {

    private val logger = Logger.getInstance(ChromeWebdriverListParser::class.java)

    override val browser = Browser.CHROME

    override fun getVersions(architecture: Architecture): List<WebdriverVersion> {
        logger.warn("Loading versions for Chrome ($architecture)")
        val doc = Jsoup.connect("https://getwebdriver.com/chromedriver").get()

        val channels = listOf("stable", "beta", "dev")
        return channels.map { channel ->
            val section = doc.getElementById(channel)
            val row = section?.getElementsByTag("tr")
                ?.toList()
                ?.filter { row ->
                    row.getElementsByTag("code").toList()
                        .any { it.text() == "chromedriver" }
                }
                ?.firstOrNull {
                    it.getElementsByTag("code")
                        .toList()
                        .any { it.text() == archToLabel(architecture) }
                } ?: return emptyList()
            val url = row.getElementsByTag("code")
                .toList()
                .map { it.text().trim() }
                .first { it.startsWith("https") }
            val version = section.getElementsByTag("p")
                .toList()
                .first()
                .text()
                .replace("Version: ", "")
                .trim()
            WebdriverVersion("$version ($channel)", url)
        }
    }

    private fun archToLabel(architecture: Architecture) = when (architecture) {
        Architecture.MAC_ARM64 -> "mac-arm64"
        Architecture.MAC_X64 -> "mac-x64"
        Architecture.WIN32 -> "win32"
        Architecture.WIN64 -> "win64"
        Architecture.LINUX64 -> "linux64"
        else -> throw IllegalArgumentException("Architecture $architecture not supported")
    }
}