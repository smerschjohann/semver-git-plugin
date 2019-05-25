package io.wusa

import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

class SemanticVersionFactory : IVersionFactory {
    private val LOG = LoggerFactory.getLogger(SemanticVersionFactory::class.java)

    override fun createFromString(describe: String, project: Project): Version {
        val suffixRegex = """(?:-(?<count>[0-9]+)(?:-g(?<sha>[0-9a-f]{1,7}))(?<dirty>-dirty)?)$""".toRegex()
        val suffix = suffixRegex.find(describe)
                ?.destructured
                ?.let { (count, sha, dirty) ->
                    Suffix(count.toInt(), sha, dirty.isNotEmpty())
                }

        var version = describe
        if (suffix != null) {
            version = suffixRegex.replace(describe, "")
        }

        val parsedVersion = parseVersion(version, project)
        parsedVersion.suffix = suffix
        return parsedVersion
    }

    private fun parseVersion(version: String, project: Project): Version {
        val versionRegex = """^[vV]?(?<major>0|[1-9]\d*)\.(?<minor>0|[1-9]\d*)\.(?<patch>0|[1-9]\d*)(?:-(?<prerelease>(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+(?<build>[a-zA-Z0-9][a-zA-Z0-9\.-]+)?)?$""".toRegex()

        return versionRegex.matchEntire(version)
                ?.destructured
                ?.let { (major, minor, patch, prerelase, build) ->
                    Version(major.toInt(), minor.toInt(), patch.toInt(), prerelase, build, null, project)
                }
                ?: throw IllegalArgumentException("Bad input '$version'")
    }
}