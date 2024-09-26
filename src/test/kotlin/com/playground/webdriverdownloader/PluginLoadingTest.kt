package com.playground.webdriverdownloader

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PluginLoadingTest : BasePlatformTestCase() {

    @Test
    fun `plugin loading`() {
        val pluginId = "com.playground.webdriver-downloader"
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginId))
        assertNotNull("Plugin with ID '$pluginId' has not been loaded", plugin)
    }

}