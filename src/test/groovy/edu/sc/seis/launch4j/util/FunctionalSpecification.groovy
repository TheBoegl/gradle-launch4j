package edu.sc.seis.launch4j.util

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

@CompileStatic
class FunctionalSpecification extends Specification {
    private final static boolean DEBUG = Boolean.parseBoolean(System.getProperty("org.gradle.testkit.debug", "false"))

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File projectDir
    File buildFile

    List<File> pluginClasspath

    def setup() {
        projectDir = testProjectDir.root
        buildFile = testProjectDir.newFile('build.gradle')

        def pluginClasspathResource = ((URLClassLoader) FunctionalSpecification.class.classLoader).findResource('plugin-classpath.txt')
        if (pluginClasspathResource == null) {
            throw new IllegalStateException('Plugin classpath resource file not found. Run the "testClasses" task.')
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }
        """
    }

    protected BuildResult build(String... arguments) {
        createAndConfigureGradleRunner(arguments).build()
    }

    protected GradleRunner createAndConfigureGradleRunner(String... arguments) {
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withDebug(DEBUG)
            .withPluginClasspath(pluginClasspath)
            .withArguments(arguments)
    }
}
