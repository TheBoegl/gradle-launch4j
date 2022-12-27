/*
 * Copyright (c) 2019 Sebastian Boegl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package edu.sc.seis.launch4j.util

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
class FunctionalSpecification extends Specification {
    private final static boolean DEBUG = Boolean.getBoolean("org.gradle.testkit.debug")

    @TempDir
    Path testProjectDir;

    File projectDir
    File buildFile

    def setup() {
        projectDir = testProjectDir.toFile()
        buildFile = testProjectDir.resolve('build.gradle').toFile()

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
            .withPluginClasspath()
            .withArguments(arguments)
    }

    protected File newFolder(String first, String... more) {
        def path = testProjectDir.resolve(Paths.get(first, more))
        Files.createDirectories(path)
        return path.toFile()
    }

    protected File newFile(String fileName) {
        return testProjectDir.resolve(fileName).toFile()
    }
}
