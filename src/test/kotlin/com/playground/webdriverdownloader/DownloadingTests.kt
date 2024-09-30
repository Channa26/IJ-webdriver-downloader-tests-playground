package com.playground.webdriverdownloader

import com.intellij.openapi.project.Project
import org.junit.After
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.nio.file.Paths

class DownloadingTests {

    private val versionToDownload = "v0.35.0"
    private val expectedFilename = "geckodriver-$versionToDownload-linux64.tar.gz"
    private val sampleUrl = "https://github.com/mozilla/geckodriver/releases/download/$versionToDownload/$expectedFilename"

    @Test
    fun `webdriver downloading`() {
        val downloader = DownloaderServiceImpl(mockProject)

        val file = downloader.downloadWebdriver(sampleUrl)
        val relativePath = file.absolutePath
            .replace(mockProject.basePath!!, "")
            .replace("\\", "/")
            .replace("^/".toRegex(), "")

        Assert.assertEquals(
            "check relative path of a downloaded file",
            ".webdrivers/$expectedFilename",
            relativePath,
        )
    }

    @After
    fun tearDown() {
        val file = mockProject.basePath!! + "/.webdrivers/$expectedFilename"
        File(file).delete()
    }

    /**
     * Mock Project instance using Mockito so that DownloaderServiceImpl is able to get some path to download
     * a webdriver to.
     */
    companion object {

        private val mockProject = Mockito.mock(Project::class.java)

        @JvmStatic
        @BeforeClass
        fun setUp(): Unit {
            Mockito.`when`(mockProject.basePath).thenAnswer {
                Paths.get("").toAbsolutePath().toString()
            }
        }

    }

}