package com.playground.webdriverdownloader.parsing

import com.intellij.openapi.diagnostic.Logger
import com.playground.webdriverdownloader.Architecture
import com.playground.webdriverdownloader.Browser
import com.playground.webdriverdownloader.WebdriverVersion
import org.jsoup.Jsoup

class FirefoxWebdriverListParser : WebdriverListParser {

    override val browser = Browser.FIREFOX

    private val logger = Logger.getInstance(FirefoxWebdriverListParser::class.java)

    override fun getVersions(architecture: Architecture): List<WebdriverVersion> {
        logger.warn("Loading versions for Firefox ($architecture)")
        val doc = Jsoup.connect("https://github.com/mozilla/geckodriver/releases").get()
        val firstLinksContainer = doc.getElementsByClass("Box-footer").first()!!
        val linkNodes = firstLinksContainer.getElementsByTag("a").toList()
        val mostRecentLink = (linkNodes.find { it.text().contains(archToLabel(architecture) + ".") } ?: return emptyList())
            .attr("href")
            .replace("^/".toRegex(), "https://github.com/")
        val versionNumbers = doc.getElementsByClass("Link--primary Link")
            .map { it.text() }
            .filter { it.matches("\\d+\\.\\d+\\.\\d+".toRegex()) }
        val mostRecentVersion = versionNumbers.first()
        return versionNumbers.map { number ->
            WebdriverVersion(
                number,
                mostRecentLink.replace(mostRecentVersion, number),
            )
        }
    }

    private fun archToLabel(architecture: Architecture) = when (architecture) {
        Architecture.MAC_ARM64 -> "macos-aarch64"
        Architecture.MAC_X64 -> "macos"
        Architecture.WIN32 -> "win32"
        Architecture.WIN64 -> "win32" // there is not x64 version available
        Architecture.LINUX64 -> "linux64"
        else -> throw IllegalArgumentException("Architecture $architecture not supported")
    }

}