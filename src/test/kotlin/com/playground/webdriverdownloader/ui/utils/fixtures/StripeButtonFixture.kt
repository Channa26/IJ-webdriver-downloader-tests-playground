package com.playground.webdriverdownloader.ui.utils.fixtures

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.search.locators.Locator
import com.intellij.remoterobot.utils.Locators
import com.intellij.toolWindow.StripeButton

@FixtureName("StripeButton")
class StripeButtonFixture(
    remoteRobot: RemoteRobot,
    remoteComponent: RemoteComponent,
) : ComponentFixture(remoteRobot, remoteComponent) {

    companion object {

        fun byLabel(label: String): Locator {
            return Locators.byTypeAndProperties(StripeButton::class.java, Locators.XpathProperty.TEXT to label)
        }

    }

    fun isVisible(): Boolean {
        return callJs("component.isVisible();", true)
    }

}