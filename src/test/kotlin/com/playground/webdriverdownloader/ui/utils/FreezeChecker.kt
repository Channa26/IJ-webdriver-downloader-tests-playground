package com.playground.webdriverdownloader.ui.utils

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

fun checkForFreezes(block: () -> Unit): List<UiFreeze> {
    val currentDirPath = Paths.get("").toAbsolutePath()
    val logPath = currentDirPath.resolve(
        Paths.get("build", "idea-sandbox", "system", "log", "idea.log")
    )
    val logFile = File(logPath.absolutePathString())
    val logBefore = logFile.readText().lines()
    block()
    val logAfter = logFile.readText().lines()

    val logDiff = logAfter.filterNot { it in logBefore }
    val regex = Regex("""UI was frozen for (\d+)ms""")
    return logDiff.mapNotNull { regex.find(it) }
        .map { UiFreeze(it.groupValues[1].toInt())}
}

data class UiFreeze(val durationMs: Int)