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

package edu.sc.seis.launch4j

import groovy.transform.AutoClone
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.logging.Logger
import org.gradle.api.model.ObjectFactory
import org.gradle.api.model.ReplacedBy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.util.GradleVersion

import javax.inject.Inject
import java.nio.file.Path
import java.util.concurrent.Callable

// do not compile static because this will break the layout#directoryProperty() for gradle version 4.9 to 5.1.
@AutoClone
class Launch4jPluginExtension implements Launch4jConfiguration {

    final FileOperations fileOperations
    final ObjectFactory objectFactory
    final Logger logger
    @Internal
    final Provider<String> targetCompatibility
    @Internal
    final Provider<String> sourceCompatibility
    @Internal
    final Provider<FileCollection> jarFileCollection

    @Inject
    Launch4jPluginExtension(Project project, FileOperations fileOperations, ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.fileOperations = fileOperations
        this.objectFactory = objectFactory
        logger = project.logger
        targetCompatibility = asGradleProperty(project, providerFactory, 'targetCompatibility')
        sourceCompatibility = asGradleProperty(project, providerFactory, 'sourceCompatibility')
        jarFileCollection = project.tasks.named(JavaPlugin.JAR_TASK_NAME).map {it?.outputs?.files?: null }
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
        requires64Bit = objectFactory.property(Boolean)
        jreMinVersion = objectFactory.property(String)
        jreMaxVersion = objectFactory.property(String)
        requiresJdk = objectFactory.property(Boolean)
        variables = objectFactory.setProperty(String)
        mutexName = objectFactory.property(String)
        windowTitle = objectFactory.property(String)
        messagesStartupError = objectFactory.property(String)
        messagesJreNotFoundError = objectFactory.property(String)
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
            requires64Bit.convention(false)
            // unable to get correct jreMinVersion here
            requiresJdk.convention(false)
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
            outputDirectory = project.layout.directoryProperty()
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
            requires64Bit.set(false)
            // unable to get correct jreMinVersion here
            requiresJdk.set(false)
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
        dest = outputDirectory.file(outfile)
        xmlFile = outputDirectory.file(xmlFileName)
        libraryDirectory = outputDirectory.file(libraryDir)
    }

    private static Provider<String> asGradleProperty(Project project, ProviderFactory providerFactory, String propertyName) {
//        providerFactory.gradleProperty(propertyName) // should be working as of gradle 6.2, but the value is not available.
        def provider = providerFactory.provider(new Callable<String>() {
            @Override
            String call() throws Exception {
                return project.hasProperty(propertyName) ? project.property(propertyName) : null
            }
        })
        if (GradleVersion.current() >= GradleVersion.version("6.5") && GradleVersion.current() <= GradleVersion.version("7.4")) {
            provider.forUseAtConfigurationTime()
        } else {
            provider
        }
    }

    final Property<String> mainClassName
    final Property<Task> jarTask

    @Input
    final Property<String> outputDir

    DirectoryProperty outputDirectory

    final Property<String> libraryDir
    @Internal
    final Provider<RegularFile> libraryDirectory
    final Property<String> xmlFileName
    final Property<Boolean> dontWrapJar
    final Property<String> headerType

    final Property<String> outfile
    final Provider<RegularFile> dest
    final Property<String> errTitle
    final Property<String> cmdLine
    final Property<String> chdir
    final Property<String> priority
    final Property<String> downloadUrl
    final Property<String> supportUrl
    final Property<Boolean> stayAlive
    final Property<Boolean> restartOnCrash
    final Property<DuplicatesStrategy> duplicatesStrategy
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
    final Property<Boolean> requires64Bit
    final Property<String> jreMinVersion

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion.isPresent()) {
            String current
            if (targetCompatibility.isPresent()) {
                current = targetCompatibility.get()
            } else if (sourceCompatibility.isPresent()) {
                // not hit in the tests as targetCompatibility is always set
                current = sourceCompatibility.get()
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
    final Property<Boolean> requiresJdk
    final SetProperty<String> variables

    final Property<String> mutexName
    final Property<String> windowTitle
    final Property<String> messagesStartupError
    final Property<String> messagesJreNotFoundError
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

    final Provider<RegularFile> xmlFile

    @Override
    Path getJarTaskOutputPath() {
        return jarTask.getOrNull().outputs?.files?.singleFile?.toPath()
    }

    @Override
    Path getJarTaskDefaultOutputPath() {
        if(jarFileCollection.isPresent() && jarFileCollection.get()) {
            jarFileCollection.get().singleFile.toPath()
        }
        return null
    }

    final SetProperty<String> classpath

    @Deprecated
    @ReplacedBy("requiresJdk")
    String getJdkPreference() {
        logger.warn("use requiresJdk instead of jdkPreference")
        requiresJdk.getOrElse(false) ? 'jdkOnly' : null
    }

    @Deprecated
    void setJdkPreference(String message) {
        logger.warn("use requiresJdk instead of jdkPreference")
        requiresJdk.set('jdkOnly'.equalsIgnoreCase(message))
    }


    @Deprecated
    @ReplacedBy("requires64Bit")
    String getRuntimeBits() {
        logger.warn("use requires64Bit instead of runtimeBits")
        requires64Bit.getOrElse(false) ? '64' : null
    }

    @Deprecated
    void setRuntimeBits(String value) {
        logger.warn("use requires64Bit instead of runtimeBits")
        requires64Bit.set('64' == value)
    }

    @Deprecated
    @ReplacedBy("messagesJreNotFoundError")
    String getMessagesBundledJreError() {
        logger.warn("use messagesJreNotFoundError instead of messagesBundledJreError")
        messagesJreNotFoundError.getOrNull()
    }

    @Deprecated
    void setMessagesBundledJreError(String message) {
        logger.warn("use messagesJreNotFoundError instead of messagesBundledJreError")
        messagesJreNotFoundError.set(message)
    }
}
