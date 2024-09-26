package com.playground.webdriverdownloader

import com.playground.webdriverdownloader.parsing.ChromeWebdriverListParser
import com.playground.webdriverdownloader.parsing.FirefoxWebdriverListParser
import org.assertj.core.api.JUnitSoftAssertions
import org.junit.Rule
import org.junit.Test

class WebdriverListParserTests {

    /**
     * Added soft-assertions to check all available options and generate a general report instead of failing
     * on the first assertions and ignoring the rest of them.
     */
    @JvmField
    @Rule
    val softly = JUnitSoftAssertions()

    @Test
    fun `list parser tests`() {
        val parsers = listOf(
            ChromeWebdriverListParser(),
            FirefoxWebdriverListParser(),
        )
        parsers.forEach { parser ->
            Architecture.values().forEach { architecture ->
                val versions = parser.getVersions(architecture)
                softly.assertThat(versions).`as`("Check downloaded versions for ${parser.browser} ($architecture)").isNotEmpty
            }
        }
        softly.assertAll()
    }

}
