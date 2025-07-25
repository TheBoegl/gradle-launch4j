/*
 * Copyright (c) 2025 Sebastian Boegl
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
import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@CompileStatic
class FunctionalSpecification extends Specification {
    private static final boolean DEBUG = Boolean.getBoolean("org.gradle.testkit.debug")
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor()

    @TempDir
    protected Path testProjectDir

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = testProjectDir.toFile()
        buildFile = Files.createFile(testProjectDir.resolve( 'build.gradle')).toFile()

        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }
        """
        settingsFile = newFile('settings.gradle')
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
        buildFile << """
tasks.withType(edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask.class).configureEach {
    stayAlive = true
}
"""
        if (!new File(projectDir, 'src').exists()) {
            addMainAndUpdateManifest()
        }
        if (!buildFile.text.contains("internalName")) {
            shortenInternalNameIfTooLong()
        }
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withDebug(DEBUG)
            .withPluginClasspath()
            .withArguments(arguments)
    }

    protected File newFolder(String... folders) {
        Path path = testProjectDir
        for (final def folder in folders) {
            path = path.resolve(folder)
        }
        return Files.createDirectories(path).toFile()
    }

    protected File newFile(String file) {
        testProjectDir.resolve(file).toFile()
    }

    protected String getExpectedJavaVersion() {
        getExpectedJavaVersion(JavaVersion.current())
    }

    protected String getExpectedJavaVersion(JavaVersion version) {
        version.isCompatibleWith(JavaVersion.VERSION_1_9) ?  "${version}.0.0" :  "${version}.0"
    }

    void shortenInternalNameIfTooLong() {
        String name = projectDir.getName();
        int maxLength = 50
        if (name.length() > maxLength) {
            buildFile << "launch4j.internalName = '${name.substring(0, maxLength)}'"
        }
    }


    protected addMainAndUpdateManifest() {
        new File(newFolder('src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("...");
                }
            }
"""

        buildFile << """
tasks.withType(Jar) {
    manifest {
        attributes 'Main-Class': 'com.test.app.Main'
    }
}
"""
    }
}
