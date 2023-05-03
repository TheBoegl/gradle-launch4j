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
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
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
        ObjectFactory objectFactory = project.objects
        mainClassName = objectFactory.property(String)
        jarTask = objectFactory.property(Task)
        outputDir = objectFactory.property(String)
        dontWrapJar = objectFactory.property(Boolean)
        outfile = objectFactory.property(String)
        xmlFileName = objectFactory.property(String)
        libraryDir = objectFactory.property(String)
        headerType = objectFactory.property(String)
        errTitle = objectFactory.property(String)
        cmdLine = objectFactory.property(String)
        chdir = objectFactory.property(String)
        priority = objectFactory.property(String)
        downloadUrl = objectFactory.property(String)
        supportUrl = objectFactory.property(String)
        stayAlive = objectFactory.property(Boolean)
        restartOnCrash = objectFactory.property(Boolean)
        duplicatesStrategy = objectFactory.property(DuplicatesStrategy)
        manifest = objectFactory.property(String)
        icon = objectFactory.property(String)
        version = objectFactory.property(String)
        textVersion = objectFactory.property(String)
        copyright = objectFactory.property(String)
        jvmOptions = objectFactory.setProperty(String)
        companyName = objectFactory.property(String)
        fileDescription = objectFactory.property(String)
        productName = objectFactory.property(String)
        internalName = objectFactory.property(String)
        trademarks = objectFactory.property(String)
        language = objectFactory.property(String)
        bundledJrePath = objectFactory.property(String)
        bundledJre64Bit = objectFactory.property(Boolean)
        bundledJreAsFallback = objectFactory.property(Boolean)
        jreMinVersion = objectFactory.property(String)
        jreMaxVersion = objectFactory.property(String)
        jdkPreference = objectFactory.property(String)
        jreRuntimeBits = objectFactory.property(String)
        variables = objectFactory.setProperty(String)
        mutexName = objectFactory.property(String)
        windowTitle = objectFactory.property(String)
        messagesStartupError = objectFactory.property(String)
        messagesBundledJreError = objectFactory.property(String)
        messagesJreVersionError = objectFactory.property(String)
        messagesLauncherError = objectFactory.property(String)
        messagesInstanceAlreadyExists = objectFactory.property(String)
        initialHeapSize = objectFactory.property(Integer)
        initialHeapPercent = objectFactory.property(Integer)
        maxHeapSize = objectFactory.property(Integer)
        maxHeapPercent = objectFactory.property(Integer)
        splashFileName = objectFactory.property(String)
        splashWaitForWindows = objectFactory.property(Boolean)
        splashTimeout = objectFactory.property(Integer)
        splashTimeoutError = objectFactory.property(Boolean)
        copyConfigurable = objectFactory.property(Object)
        classpath = objectFactory.setProperty(String)
        // use named without class to be compatible with gradle 4.9
        def javaJarTask = project.tasks.named(JavaPlugin.JAR_TASK_NAME)
        def defaultOutputDir = 'launch4j'
        def isPropertyConventionSupported = GradleVersion.current() >= GradleVersion.version("5.1")
        if (isPropertyConventionSupported) {
            jarTask.convention(javaJarTask)
            outputDir.convention(defaultOutputDir)
            outputDirectory = objectFactory.directoryProperty().convention(project.layout.buildDirectory.dir(outputDir))
            libraryDir.convention('lib')
            xmlFileName.convention('launch4j.xml')
            dontWrapJar.convention(false)
            headerType.convention('gui')
            outfile.convention("${project.name}.exe")
            errTitle.convention('')
            cmdLine.convention('')
            chdir.convention('.')
            priority.convention('normal')
            downloadUrl.convention('http://java.com/download')
            supportUrl.convention('')
            stayAlive.convention(false)
            restartOnCrash.convention(false)
            duplicatesStrategy.convention(DuplicatesStrategy.EXCLUDE)
            manifest.convention('')
            icon.convention('')
            version.convention("${project.version}")
            textVersion.convention("${project.version}")
            copyright.convention('unknown')
            jvmOptions.convention([])
            companyName.convention('')
            fileDescription.convention("${project.name}")
            productName.convention("${project.name}")
            internalName.convention("${project.name}")
            trademarks.convention('')
            language.convention('ENGLISH_US')
            bundledJre64Bit.convention(false)
            bundledJreAsFallback.convention(false)
            // unable to get correct jreMinVersion here
            jdkPreference.convention('preferJre')
            jreRuntimeBits.convention('64/32')
            variables.convention([])
            initialHeapSize.convention(null)
            initialHeapPercent.convention(null)
            maxHeapSize.convention(null)
            maxHeapPercent.convention(null)
            splashWaitForWindows.convention(true)
            splashTimeout.convention(60)
            splashTimeoutError.convention(true)
            copyConfigurable.convention(null)
            classpath.convention([])
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
            libraryDir.set('lib')
            xmlFileName.set('launch4j.xml')
            dontWrapJar.set(false)
            headerType.set('gui')
            outfile.set(project.name + '.exe')
            errTitle.set('')
            cmdLine.set('')
            chdir.set('.')
            priority.set('normal')
            downloadUrl.set('http://java.com/download')
            supportUrl.set('')
            stayAlive.set(false)
            restartOnCrash.set(false)
            duplicatesStrategy.set(DuplicatesStrategy.EXCLUDE)
            manifest.set('')
            icon.set('')
            version.set(String.valueOf(project.version))
            textVersion.set(String.valueOf(project.version))
            copyright.set('unknown')
            jvmOptions.set([])
            companyName.set('')
            fileDescription.set(project.name)
            productName.set(project.name)
            internalName.set(project.name)
            trademarks.set('')
            language.set('ENGLISH_US')
            bundledJre64Bit.set(false)
            bundledJreAsFallback.set(false)
            // unable to get correct jreMinVersion here
            jdkPreference.set('preferJre')
            jreRuntimeBits.set('64/32')
            variables.set([])
            initialHeapSize.set(null)
            initialHeapPercent.set(null)
            maxHeapSize.set(null)
            maxHeapPercent.set(null)
            splashWaitForWindows.set(true)
            splashTimeout.set(60)
            splashTimeoutError.set(true)
            copyConfigurable.set(null)
            classpath.set([])
        }
    }

    final Property<String> mainClassName
    final Property<Task> jarTask

    @Input
    final Property<String> outputDir

    DirectoryProperty outputDirectory

    final Property<String> libraryDir
    final Property<String> xmlFileName
    final Property<Boolean> dontWrapJar
    final Property<String> headerType

    final Property<String> outfile

    File getDest() {
        project.file("${getOutputDirectory()}/${outfile}")
    }
    final Property<String> errTitle
    final Property<String> cmdLine
    final Property<String> chdir
    final Property<String> priority
    final Property<String> downloadUrl
    final Property<String> supportUrl
    final Property<Boolean> stayAlive
    final Property<Boolean> restartOnCrash
    final Property<DuplicatesStrategy> duplicatesStrategy
    final Property<String> manifest
    final Property<String> icon
    final Property<String> version
    final Property<String> textVersion
    final Property<String> copyright
    final SetProperty<String> jvmOptions
    final Property<String> companyName
    final Property<String> fileDescription
    final Property<String> productName
    final Property<String> internalName
    final Property<String> trademarks
    final Property<String> language

    final Property<String> bundledJrePath
    final Property<Boolean> bundledJre64Bit
    final Property<Boolean> bundledJreAsFallback
    final Property<String> jreMinVersion

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion.isPresent()) {
            String current
            if (project.hasProperty('targetCompatibility')) {
                current = project.property('targetCompatibility')
            } else if (project.hasProperty('sourceCompatibility')) {
                // not hit in the tests as targetCompatibility is always set
                current = project.property('sourceCompatibility')
            } else {
                current = JavaVersion.current()
            }
            while (current ==~ /\d+(\.\d+)?/) {
                current = current + '.0'
            }
            jreMinVersion.set(current)
        }
        jreMinVersion.get()
    }
    final Property<String> jreMaxVersion
    final Property<String> jdkPreference
    final Property<String> jreRuntimeBits

    final SetProperty<String> variables

    final Property<String> mutexName
    final Property<String> windowTitle
    final Property<String> messagesStartupError
    final Property<String> messagesBundledJreError
    final Property<String> messagesJreVersionError
    final Property<String> messagesLauncherError
    final Property<String> messagesInstanceAlreadyExists

    final Property<Integer> initialHeapSize
    final Property<Integer> initialHeapPercent
    final Property<Integer> maxHeapSize
    final Property<Integer> maxHeapPercent

    final Property<String> splashFileName
    final Property<Boolean> splashWaitForWindows
    final Property<Integer> splashTimeout
    final Property<Boolean> splashTimeoutError

    transient final Property<Object> copyConfigurable

    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName.get()}")
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
        if (!jarTask.isPresent() && project.plugins.hasPlugin('java')) {
            return javaJarTask()
        }
        jarTask.get()
    }

    private Jar javaJarTask() {
        return project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
    }

    final SetProperty<String> classpath
}
