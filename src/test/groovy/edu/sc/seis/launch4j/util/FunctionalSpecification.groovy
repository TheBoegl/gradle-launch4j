/*
 * Copyright (c) 2023 Sebastian Boegl
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
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@CompileStatic
class FunctionalSpecification extends Specification {
    private static final boolean DEBUG = Boolean.getBoolean("org.gradle.testkit.debug")
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor()

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File projectDir
    File buildFile

    def setup() {
        projectDir = testProjectDir.root
        buildFile = testProjectDir.newFile('build.gradle')

        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }
        """
    }

    def cleanup() {
        deleteDirectory(projectDir)
    }

    static void delete(File file) {
        if (!file.delete()) {
            enqueueDeletion(file)
        }
    }

    def deleteDirectory(File folder) {
        File[] allContents = folder.listFiles()
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file)
            }
        }
        if (folder.name.endsWith('.jar') || folder.name.endsWith('.exe')) {
            // delay file deletion to avoid `Error: Unable to access jarfile` errors
            enqueueDeletion(folder)
        } else {
            delete(folder)
        }
    }

    private static void enqueueDeletion(File file) {
        EXECUTOR.schedule(new Runnable() {
            @Override
            void run() {
                delete(file)
            }
        }, 10, TimeUnit.SECONDS)
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
}
