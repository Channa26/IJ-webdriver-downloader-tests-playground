package com.playground.webdriverdownloader

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import webdriverdownloader.forms.DownloadForm

class MainViewFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val service = project.service<DownloaderService>()
        val downloadForm = DownloadForm(service)
        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(downloadForm.rootPanel, "", true)
        contentManager.addContent(content)
    }
}