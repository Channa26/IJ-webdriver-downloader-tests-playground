package com.playground.webdriverdownloader

import java.io.File

interface DownloaderService {

    fun downloadWebdriver(url: String): File
    fun loadWebdriverVersions(browser: Browser): List<WebdriverVersion>

}