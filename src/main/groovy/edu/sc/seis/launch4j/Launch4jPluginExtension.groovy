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

package edu.sc.seis.launch4j

import groovy.transform.AutoClone
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.GradleVersion

import java.nio.file.Path
// do not compile static because this will break the layout#directoryProperty() for gradle version 4.3 to 5.1.
@AutoClone
class Launch4jPluginExtension implements Launch4jConfiguration {

    private final Project project
    final FileOperations fileOperations

    Launch4jPluginExtension(Project project, FileOperations fileOperations) {
        this.project = project
        this.fileOperations = fileOperations
        jarTask = project.objects.property(Task)
        outputDir = project.objects.property(String)
        // use named without class to be compatible with gradle 4.9
        def javaJarTask = project.tasks.named(JavaPlugin.JAR_TASK_NAME)
        def defaultOutputDir = 'launch4j'
        def isPropertyConventionSupported = GradleVersion.current() >= GradleVersion.version("5.1")
        if (isPropertyConventionSupported) {
            jarTask.convention(javaJarTask)
            outputDir.convention(defaultOutputDir)
            outputDirectory = project.objects.directoryProperty().convention(project.layout.buildDirectory.dir(outputDir))
        } else {
            def hasLayoutsDirectoryProperty = GradleVersion.current() >= GradleVersion.version("4.3")
            if (hasLayoutsDirectoryProperty) {
                outputDirectory = project.layout.directoryProperty()
            } else {
                throw new IllegalStateException("at least gradle 4.3 is required for this plugin to work and provide org.gradle.api.provider.Property")
            }
            jarTask.set(javaJarTask)
            outputDir.set(defaultOutputDir)
            outputDirectory.set(project.layout.buildDirectory.dir(outputDir))
        }
    }

    String mainClassName
    @Deprecated
    String jar
    final Property<Task> jarTask

    @Input
    Property<String> outputDir

    DirectoryProperty outputDirectory

    String libraryDir = 'lib'
    String xmlFileName = 'launch4j.xml'
    Boolean dontWrapJar = false
    String headerType = 'gui'

    String outfile = "${project.name}.exe"

    File getDest() {
        project.file("${getOutputDirectory()}/${outfile}")
    }
    String errTitle = ''
    String cmdLine = ''
    String chdir = '.'
    String priority = 'normal'
    String downloadUrl = 'http://java.com/download'
    String supportUrl = ''
    Boolean stayAlive = false
    Boolean restartOnCrash = false
    DuplicatesStrategy duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    String manifest = ''
    String icon = ''
    String version = "${project.version}"
    String textVersion = "${project.version}"
    String copyright = 'unknown'

    Set<String> jvmOptions = []

    @Deprecated
    void setOpt(String opt) {
        if (!opt) return // null check
        this.jvmOptions = [ opt ] as Set
        project.logger.warn("${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.opt property is deprecated. Use ${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.jvmOptions instead.")
    }

    String companyName = ''
    String fileDescription = "${project.name}"

    @Deprecated
    void setDescription(String description) {
        fileDescription = description
        project.logger.warn("${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.description property is deprecated. Use ${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.fileDescription instead.")
    }

    @Deprecated
    String getDescription() {
        return fileDescription
    }
    String productName = "${project.name}"
    String internalName = "${project.name}"
    String trademarks = ''
    String language = 'ENGLISH_US'

    String bundledJrePath
    Boolean bundledJre64Bit = false
    Boolean bundledJreAsFallback = false
    String jreMinVersion

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion) {
            if (project.hasProperty('targetCompatibility')) {
                jreMinVersion = project.property('targetCompatibility')
            } else {
                jreMinVersion = JavaVersion.current()
            }
            while (jreMinVersion ==~ /\d+(\.\d+)?/) {
                jreMinVersion = jreMinVersion + '.0'
            }
        }
        jreMinVersion
    }
    String jreMaxVersion
    String jdkPreference = 'preferJre'
    String jreRuntimeBits = '64/32'

    Set<String> variables = []

    String mutexName
    String windowTitle

    String messagesStartupError
    String messagesBundledJreError
    String messagesJreVersionError
    String messagesLauncherError
    String messagesInstanceAlreadyExists

    Integer initialHeapSize
    Integer initialHeapPercent
    Integer maxHeapSize
    Integer maxHeapPercent

    String splashFileName
    Boolean splashWaitForWindows = true
    Integer splashTimeout = 60
    Boolean splashTimeoutError = true

    transient Object copyConfigurable

    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName}")
    }

    @Override
    Path getJarTaskOutputPath() {
        return jarTask.getOrNull().outputs?.files?.singleFile?.toPath()
    }

    @Override
    Path getJarTaskDefaultOutputPath() {
        if (project.plugins.hasPlugin('java')) {
            return javaJarTask()?.outputs?.files?.singleFile?.toPath()
        }
        return null
    }

    Task internalJarTask() {
        if (!jarTask && project.plugins.hasPlugin('java')) {
            return javaJarTask()
        }
        jarTask.get()
    }

    private Jar javaJarTask() {
        return project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
    }

    Set<String> classpath = []
}
