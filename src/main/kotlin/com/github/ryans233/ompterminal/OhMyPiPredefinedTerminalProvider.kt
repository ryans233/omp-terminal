package com.github.ryans233.ompterminal

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresReadLockAbsence
import org.jetbrains.plugins.terminal.TerminalToolWindowManager
import org.jetbrains.plugins.terminal.ui.OpenPredefinedTerminalActionProvider

/**
 * Registers "oh-my-pi" in the terminal's predefined session dropdown
 * (the arrow button next to the "+" new-tab button).
 */
class OhMyPiPredefinedTerminalProvider : OpenPredefinedTerminalActionProvider {

    @RequiresBackgroundThread
    @RequiresReadLockAbsence
    override fun listOpenPredefinedTerminalActions(project: Project): List<AnAction> {
        return listOf(OhMyPiAction())
    }

    private class OhMyPiAction : AnAction("oh-my-pi", "Open a new terminal tab and run omp", null), DumbAware {

        override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabledAndVisible = e.project != null
        }

        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            TerminalToolWindowManager.getInstance(project)
                .createNewSession(null, "oh-my-pi", listOf("omp"), true, true)
        }
    }
}
