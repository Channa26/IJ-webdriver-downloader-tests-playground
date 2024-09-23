package com.playground.webdriverdownloader.parsing

import com.playground.webdriverdownloader.Architecture
import com.playground.webdriverdownloader.Browser
import com.playground.webdriverdownloader.WebdriverVersion

interface WebdriverListParser {
    val browser: Browser
    fun getVersions(architecture: Architecture): List<WebdriverVersion>
}
