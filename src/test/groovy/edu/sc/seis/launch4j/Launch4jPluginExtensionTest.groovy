package edu.sc.seis.launch4j

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class Launch4jPluginExtensionTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        def pluginClasspathResource = getClass().classLoader.findResource('plugin-classpath.txt')
        if (pluginClasspathResource == null) {
            throw new IllegalStateException('Plugin classpath resource file not found. Run the "testClasses" task.')
        }

        def pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // Escape backslashes in Windows paths.
                .collect { "'$it'" }
                .join(', ')

        // Add the logic under test to the test build.
        buildFile << """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }
        """
    }

    def 'Applying the plugin provides properties'() {
        given:
        buildFile << """
            repositories {
                mavenCentral()
            }

            apply plugin: 'edu.sc.seis.launch4j'

            task printProperties << {
                println launch4j.launch4jCmd
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'printProperties')
                .build()

        then:
        result.task(':printProperties').outcome == SUCCESS
        result.standardOutput.trim().equals('launch4j')
    }
}
