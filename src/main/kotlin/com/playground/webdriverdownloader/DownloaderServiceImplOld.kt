package com.playground.webdriverdownloader
//
//import com.intellij.openapi.components.Service
//import com.intellij.openapi.diagnostic.Logger
//import com.intellij.openapi.project.Project
//import com.playground.webdriverdownloader.parsing.ChromeWebdriverListParser
//import com.playground.webdriverdownloader.parsing.FirefoxWebdriverListParser
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okio.IOException
//import java.awt.event.ActionEvent
//import java.awt.event.ActionListener
//import java.io.File
//import java.io.FileOutputStream
//import javax.swing.JComboBox
//
//private val supportedBrowsers = arrayOf(Browser.CHROME, Browser.FIREFOX)
//private const val subdir = "/.webdrivers"
//private val currentArch = Architecture.getCurrent()
//
//@Service(Service.Level.PROJECT)
//class DownloaderServiceImplOld(
//    private val project: Project,
//) {
//
//    private lateinit var selectedBrowser: Browser
//    private var availableVersions: List<WebdriverVersion>? = null
//    private var selectedVersion: WebdriverVersion? = null
//
//    private val client = OkHttpClient()
//
//    private val logger = Logger.getInstance(DownloaderServiceImplOld::class.java)
//
//    init {
//        form.setBrowsersList(supportedBrowsers.map { it.label }.toTypedArray())
//        form.setBrowserSelectionListener(object : ActionListener {
//            override fun actionPerformed(e: ActionEvent?) {
//                val selectedOption = (e?.source as JComboBox<*>?)?.selectedItem as String?
//                onBrowserSelected(supportedBrowsers.first { it.label == selectedOption })
//            }
//        })
//        onBrowserSelected(supportedBrowsers.first())
//
//        form.setDownloadButtonAction(object : ActionListener {
//            override fun actionPerformed(e: ActionEvent?) {
//                onDownloadButtonClick()
//            }
//        })
//
//        form.setVersionSelectionListener(object : ActionListener {
//            override fun actionPerformed(e: ActionEvent?) {
//                onVersionSelected(availableVersions?.first { it.value == (e?.source as JComboBox<*>?)?.selectedItem as String? }!!)
//            }
//        })
//    }
//
//    private fun onDownloadButtonClick() {
//        if (selectedVersion == null) {
//            throw RuntimeException("No version selected.")
//        }
//        form.setDownloadButtonLock(true)
//        val link = selectedVersion!!.downloadLink
//        logger.warn("Downloading Webdriver for $selectedBrowser (${selectedVersion!!.value}) from $link")
//        val downloadDirPath = project.basePath + subdir
//        val downloadDir = File(downloadDirPath)
//        if (!downloadDir.exists()) {
//            downloadDir.mkdirs()
//        }
//        val request = Request.Builder()
//            .url(link)
//            .build()
//        val outputFile = File(downloadDirPath, link.split("/").last())
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Failed to download file: $response")
//            response.body?.use { body ->
//                val inputStream = body.byteStream()
//                val outputStream = FileOutputStream(outputFile)
//                inputStream.copyTo(outputStream)
//                outputStream.close()
//                inputStream.close()
//            } ?: throw IOException("Response body is null")
//        }
//        logger.warn("Downloaded")
//        form.setDownloadButtonLock(false)
//    }
//
//    private fun onBrowserSelected(browser: Browser) {
//        selectedBrowser = browser
//        logger.warn("Selected browser: $browser")
//        form.setVersionsSelectorLock(true)
//        availableVersions = null
//        selectedVersion = null
//        form.setDownloadButtonLock(true)
//
//        val versions = when (browser) {
//            Browser.CHROME -> ChromeWebdriverListParser().getVersions(currentArch)
//            Browser.FIREFOX -> FirefoxWebdriverListParser().getVersions(currentArch)
//            else -> throw RuntimeException("Unable to load versions for browser $browser.")
//        }
//        updateVersionsList(versions)
//    }
//
//    private fun updateVersionsList(versions: List<WebdriverVersion>) {
//        availableVersions = versions
//        form.setVersionsList(versions.map { it.value }.toTypedArray())
//        form.setVersionsSelectorLock(false)
//        onVersionSelected(versions.first())
//    }
//
//    private fun onVersionSelected(version: WebdriverVersion) {
//        logger.warn("Selected version: $version")
//        selectedVersion = version
//        form.setDownloadButtonLock(false)
//    }
//
//}
//
enum class Browser(val label: String) {
    CHROME("Chrome"),
    FIREFOX("Firefox"),
}

enum class Architecture {
    WIN64,
    WIN32,
    MAC_ARM64,
    MAC_X64,
    LINUX64;

    companion object {
        fun getCurrent(): Architecture {
            val osName = System.getProperty("os.name").lowercase()
            val osArch = System.getProperty("os.arch").lowercase()
            return when {
                osName.contains("win") && osArch.contains("64") -> WIN64
                osName.contains("win") && osArch.contains("32") -> WIN32
                osName.contains("mac") && (osArch == "aarch64" || osArch == "arm64") -> MAC_ARM64
                osName.contains("mac") && osArch.contains("x86_64") -> MAC_X64
                osName.contains("nix") || osName.contains("nux") || osName.contains("aix") && osArch.contains("64") -> LINUX64
                else -> throw UnsupportedOperationException("Unsupported architecture: $osName $osArch")
            }
        }
    }
}

data class WebdriverVersion(
    val value: String,
    val downloadLink: String,
)