package com.playground.webdriverdownloader.ui.utils

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.utils.waitForIgnoringError
import java.nio.file.Path
import java.time.Duration
import kotlin.io.path.absolutePathString

fun withRobotContext(projectPath: Path, block: RobotContext.(remoteRobot: RemoteRobot) -> Unit) {
    val context = RobotContext(projectPath)
    block(context, RobotContext.remoteRobot)
}

/**
 * JS-commands for the IDE are partially copied from
 * https://github.com/JetBrains/intellij-ui-test-robot/blob/master/remote-fixtures/src/main/kotlin/com/intellij/remoterobot/steps/CommonSteps.kt
 */
class RobotContext(private val projectPath: Path) {

    fun waitForIde() {
        waitForIgnoringError(duration = Duration.ofSeconds(30)) {
            remoteRobot.callJs("true")
        }
        //closeProject()
        //openProject()
    }

    fun openProject() {
        remoteRobot.runJs(
            """
            importClass(com.intellij.openapi.application.ApplicationManager)
            importClass(com.intellij.ide.impl.OpenProjectTask)
           
            const projectManager = com.intellij.openapi.project.ex.ProjectManagerEx.getInstanceEx()
            let task 
            try { 
                task = OpenProjectTask.build()
            } catch(e) {
                task = OpenProjectTask.newProject()
            }
            const path = new java.io.File("${projectPath.absolutePathString()}").toPath()
           
            const openProjectFunction = new Runnable({
                run: function() {
                    projectManager.openProject(path, task)
                }
            })
           
            ApplicationManager.getApplication().invokeLater(openProjectFunction)
        """
        )
    }

    fun closeProject() {
        invokeAction("CloseProject")
    }

    fun invokeAction(actionId: String) {
        remoteRobot.runJs(
            """
            importClass(com.intellij.openapi.application.ApplicationManager)
            
            const actionId = "$actionId";
            const actionManager = com.intellij.openapi.actionSystem.ActionManager.getInstance();
            const action = actionManager.getAction(actionId);
            
            const runAction = new Runnable({
                run: function() {
                    actionManager.tryToExecute(action, com.intellij.openapi.ui.playback.commands.ActionCommand.getInputEvent(actionId), null, null, true);
                }
            })
            ApplicationManager.getApplication().invokeLater(runAction)
        """, true
        )
    }

    companion object {
        val remoteRobot = RemoteRobot("http://127.0.0.1:8082")
    }

}