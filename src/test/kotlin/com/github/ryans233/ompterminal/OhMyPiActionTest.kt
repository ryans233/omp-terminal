package com.github.ryans233.ompterminal

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.terminal.ui.OpenPredefinedTerminalActionProvider

class OhMyPiActionTest : BasePlatformTestCase() {

    fun `test predefined terminal provider is registered`() {
        val providers = OpenPredefinedTerminalActionProvider.EP_NAME.extensionList
        assertTrue(
            "OhMyPiPredefinedTerminalProvider should be registered",
            providers.any { it is OhMyPiPredefinedTerminalProvider }
        )
    }
}
