package io.wusa

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.wusa.incrementer.ConventionalCommitsIncrementer
import io.wusa.incrementer.VersionIncrementer
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConventionalCommitsIncrementerTest {

    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = ProjectBuilder.builder().build()
        mockkObject(GitCommandRunner)
    }

    @AfterEach
    internal fun tearDown() {
        unmockkObject(GitCommandRunner)
    }

    @Test
    fun `patch should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "7356414 fix: update gradle plugin publish due to security bugs\n" +
                "45f65f6 fix: update version an changelog\n" +
                "67f03b1 feat: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "fba5872 fix: add default tagPrefix behaviour\n" +
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "f96697f fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "73fc8b4 fix: Add support for multi-module projects.\n" +
                "74e3eb1 fix: add test for kebab-case with numbers\n" +
                "63ca60f fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 1, 0, "", "", null))
    }

    @Test
    fun `minor should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "7356414 fix: update gradle plugin publish due to security bugs\n" +
                "45f65f6 fix: update version an changelog\n" +
                "67f03b1 fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "fba5872 fix: add default tagPrefix behaviour\n" +
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "f96697f fix: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "73fc8b4 fix: Add support for multi-module projects.\n" +
                "74e3eb1 fix: add test for kebab-case with numbers\n" +
                "63ca60f fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(0, 0, 1, "", "", null))
    }

    @Test
    fun `major should be increased by 1`() {
        every { GitCommandRunner.execute(projectDir = any(), args = any()) } returns
                "7356414 fix: update gradle plugin publish due to security bugs\n" +
                "45f65f6 BREAKING CHANGE: update version an changelog\n" +
                "67f03b1 fix: Merge pull request #18 from ilovemilk/feature/support-multi-module\n" +
                "fba5872 feat: add default tagPrefix behaviour\n" +
                "2d03c4b fix: Merge pull request #17 from jgindin/support-multi-module\n" +
                "f96697f feat: Merge remote-tracking branch 'origin/feature/add-more-tests' into develop\n" +
                "73fc8b4 fix: Add support for multi-module projects.\n" +
                "74e3eb1 feat: add test for kebab-case with numbers\n" +
                "63ca60f fix: add more tests"

        Assertions.assertEquals(VersionIncrementer.getVersionIncrementerByName("CONVENTIONAL_COMMITS_INCREMENTER").increment(Version(0, 0, 0, "", "", null), project), Version(1, 0, 0, "", "", null))
    }
}
