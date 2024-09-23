package com.playground.webdriverdownloader

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.playground.webdriverdownloader.parsing.ChromeWebdriverListParser
import com.playground.webdriverdownloader.parsing.FirefoxWebdriverListParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val subdir = "/.webdrivers"

@Service(Service.Level.PROJECT)
class DownloaderServiceImpl(
    private val project: Project,
) : DownloaderService {

    private val client = OkHttpClient()
    private val logger = Logger.getInstance(DownloaderServiceImpl::class.java)
    private val architecture = Architecture.getCurrent()

    override fun downloadWebdriver(url: String): File {
        logger.warn("Downloading Webdriver from $url")
        val downloadDirPath = project.basePath + subdir
        val downloadDir = File(downloadDirPath)
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }
        val request = Request.Builder()
            .url(url)
            .build()
        val outputFile = File(downloadDirPath, url.split("/").last())
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to download file: $response")
            response.body?.use { body ->
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(outputFile)
                inputStream.copyTo(outputStream)
                outputStream.close()
                inputStream.close()
            } ?: throw IOException("Response body is null")
        }
        logger.warn("Downloaded")
        return outputFile
    }

    override fun loadWebdriverVersions(browser: Browser): List<WebdriverVersion> {
        val listParser = when (browser) {
            Browser.FIREFOX -> FirefoxWebdriverListParser()
            Browser.CHROME -> ChromeWebdriverListParser()
        }
        return listParser.getVersions(architecture)
    }

}