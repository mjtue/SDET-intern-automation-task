package com.example.uitests

import com.intellij.driver.sdk.ui.components.UiComponent.Companion.waitFound
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.button
import com.intellij.driver.sdk.ui.components.elements.checkBoxWithName
import com.intellij.driver.sdk.ui.components.settings.settingsDialog
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.IdeInfo
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.GitHubProject
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import com.intellij.ide.starter.sdk.JdkDownloaderFacade.jdk21
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SettingsUiTest {
    private val ideaUltimate = IdeInfo(
        productCode = "IU",
        platformPrefix = "idea",
        executableFileName = "idea",
        fullName = "IDEA",
        qodanaProductCode = "QDJVM"
    )

    @Test
    fun testAutoCreateChangelists() {
        val testContext = Starter
            .newContext(
                CurrentTestMethod.hyphenateWithClass(),
                TestCase(
                    ideaUltimate,
                    GitHubProject.fromGithub(
                        branchName = "master",
                        repoRelativeUrl = "jitpack/gradle-simple.git",
                        commitHash = "c11de3b42af65dd14c58d175c6ce0deb629704d6"
                    )
                )
            )
            .setupSdk(jdk21.toSdk())
            .prepareProjectCleanImport()

        testContext.runIdeWithDriver().useDriverAndCloseIde {
            ideFrame {
                waitForIndicators(5.minutes)

                // now=false is required for modal dialogs so the EDT is not blocked
                openSettingsDialog()

                settingsDialog {
                    waitFound(30.seconds)
                    openTreeSettingsSection("Version Control", "Changelists")

                    val autoCreateCheckbox = checkBoxWithName("Create changelists automatically")
                    autoCreateCheckbox.check()

                    assertTrue(autoCreateCheckbox.isSelected())

                    button("OK").click()
                }
            }
        }
    }
}
