package com.playground.webdriverdownloader.ui

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.utils.WaitForConditionTimeoutException
import com.playground.webdriverdownloader.Browser
import com.playground.webdriverdownloader.ui.utils.checkForFreezes
import com.playground.webdriverdownloader.ui.utils.fixtures.StripeButtonFixture
import com.playground.webdriverdownloader.ui.utils.withRobotContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.Duration

/**
 * For testing this plugin's UI, it doesn't matter what kind of project is opened, so let's just use the current
 * directory as a project root.
 */
private val SAMPLE_PROJECT_PATH = File("./").toPath()
private val EXPECTED_BROWSERS = setOf("Chrome", "Firefox")

class UiTests {

    @Before
    fun setUp() {
        withRobotContext(SAMPLE_PROJECT_PATH) {
            waitForIde()
        }
    }

    @Test
    fun `test UI for freezes`() {
        withRobotContext(SAMPLE_PROJECT_PATH) { robot ->
            val freezes = checkForFreezes {
                val pluginRootPanel = getPluginRootPanel(robot)
                val browserSelector =
                    pluginRootPanel.comboBox(byXpath("//div[@class='JComboBox' and @name='browserSelector']"))
                browserSelector.selectItem(EXPECTED_BROWSERS.first())

                val downloadButton = pluginRootPanel.button("Download")
                var buttonAwaitingTimeoutSec = 10
                while (!downloadButton.isEnabled() && buttonAwaitingTimeoutSec-- > 0) {
                    if (buttonAwaitingTimeoutSec <= 0) {
                        Assert.fail("Timeout during download button awaiting")
                    }
                    Thread.sleep(1000)
                }
                downloadButton.click()

                var downloadTimeoutSec = 60
                while (pluginRootPanel.getResultText()?.lowercase()
                        ?.contains("downloaded") != true && downloadTimeoutSec-- > 0
                ) {
                    if (downloadTimeoutSec <= 0) {
                        Assert.fail("Timeout during download")
                    }
                    Thread.sleep(1000)
                }
            }
            val maxFreeze = freezes.map { it.durationMs }.maxOrNull() ?: 0
            Assert.assertEquals("Check UI freeze during download", 0, maxFreeze)
        }
    }

    @Test
    fun `basic UI test for Chrome`() {
        withRobotContext(SAMPLE_PROJECT_PATH) { robot ->

            val pluginRootPanel = getPluginRootPanel(robot)

            val browserSelector =
                pluginRootPanel.comboBox(byXpath("//div[@class='JComboBox' and @name='browserSelector']"))
            val availableBrowsersOptions = browserSelector.listValues()
            Assert.assertEquals("Check available browsers count", EXPECTED_BROWSERS.size, availableBrowsersOptions.size)
            Assert.assertEquals("Check available browsers set", EXPECTED_BROWSERS, availableBrowsersOptions.toSet())

            val versionsSelector =
                pluginRootPanel.comboBox(byXpath("//div[@class='JComboBox' and @name='versionSelector']"))
            val initialAvailableVersionsOptions = versionsSelector.listValues()
            Assert.assertTrue(
                "Check that there is at least one version available",
                initialAvailableVersionsOptions.isNotEmpty()
            )

           // val currentBrowser = browserSelector.selectedText()
            browserSelector.selectItem(Browser.CHROME.label)
            var timeoutSec = 20
            var newAvailableVersionsOptions = versionsSelector.listValues()
            while (initialAvailableVersionsOptions.toSet() == newAvailableVersionsOptions.toSet() && timeoutSec-- > 0) {
                if (timeoutSec <= 0) {
                    Assert.fail("Timeout while waiting for new versions to be available")
                }
                newAvailableVersionsOptions = versionsSelector.listValues()
                Thread.sleep(1000)
            }


            Assert.assertTrue(
                "Check that there is at least one version available after update",
                newAvailableVersionsOptions.isNotEmpty()
            )

            val downloadButton = pluginRootPanel.button("Download")
            Assert.assertTrue(downloadButton.isEnabled())
            downloadButton.click()

        }
    }

    @Test
    fun `basic UI test for Firefox`() {
        withRobotContext(SAMPLE_PROJECT_PATH) { robot ->

            val pluginRootPanel = getPluginRootPanel(robot)

            val browserSelector =
                pluginRootPanel.comboBox(byXpath("//div[@class='JComboBox' and @name='browserSelector']"))
            browserSelector.selectItem(Browser.FIREFOX.label)

            val versionsSelector =
                pluginRootPanel.comboBox(byXpath("//div[@class='JComboBox' and @name='versionSelector']"))
            val initialAvailableVersionsOptions = versionsSelector.listValues()
            Assert.assertTrue(
                "Check that there is at least one version available",
                initialAvailableVersionsOptions.isNotEmpty()
            )
            versionsSelector.selectItem(initialAvailableVersionsOptions[2])


            val downloadButton = pluginRootPanel.button("Download")
            Assert.assertTrue(downloadButton.isEnabled())
            downloadButton.click()
            var downloadTimeoutSec = 60
            while (pluginRootPanel.getResultText()?.lowercase()
                    ?.contains("downloaded") != true && downloadTimeoutSec-- > 0
            ) {
                if (downloadTimeoutSec <= 0) {
                    Assert.fail("Timeout during download")
                }
                Thread.sleep(1000)
            }
            pluginRootPanel.getResultText()?.lowercase()?.contains(initialAvailableVersionsOptions[2])
                ?.let { Assert.assertTrue(it) }
//            val downloadedWebdrivers = pluginRootPanel.textArea(byXpath("//div[@class='ProjectViewTree']"))
//            downloadedWebdrivers.hasText(initialAvailableVersionsOptions[2])

        }
    }


    private fun getPluginRootPanel(robot: RemoteRobot): CommonContainerFixture {
        //language=xpath
        val xpath =
            "//div[@class='InternalDecoratorImpl'][.//div[@class='BaseLabel' and @text='WebdriverDownloader']]//div[@class='JPanel'][./div[@class='JLabel' and @text='Browser:']]"
        return try {
            robot.find(
                CommonContainerFixture::class.java,
                byXpath(xpath),
                Duration.ofSeconds(5),
            )
        } catch (e: WaitForConditionTimeoutException) {
            val panelHidingButton = robot.find(
                StripeButtonFixture::class.java,
                StripeButtonFixture.byLabel("WebdriverDownloader"),
                Duration.ofSeconds(5),
            )
            panelHidingButton.click()
            robot.find(
                CommonContainerFixture::class.java,
                byXpath(xpath),
                Duration.ofSeconds(5),
            )
        }
    }

    private fun CommonContainerFixture.getResultText(): String? {
        try {
            val resultLabel = this.jLabel(
                byXpath("//div[@class='JLabel' and @name='resultLabel']"),
                Duration.ofMillis(50),
            )
            return resultLabel.value
        } catch (e: WaitForConditionTimeoutException) {
            return null
        }
    }
}
