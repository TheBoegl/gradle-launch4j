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

package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CopyLibraries
import edu.sc.seis.launch4j.CreateXML
import edu.sc.seis.launch4j.Launch4jConfiguration
import edu.sc.seis.launch4j.Launch4jPlugin
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.model.ReplacedBy
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.util.GradleVersion

import java.nio.file.Path
// do not compile static because this will break the layout#directoryProperty() for gradle version 4.3 to 5.1.
abstract class DefaultLaunch4jTask extends DefaultTask implements Launch4jConfiguration {

    private Launch4jPluginExtension config
    private FileCollection runtimeClassFiles
    @Internal
    final Provider<Configuration> launch4jDependency
    @Internal
    final Provider<RegularFile> launch4jBinaryDirectory

    @InputFiles
    final ConfigurableFileCollection launch4jBinaryFiles

    protected DefaultLaunch4jTask() {
        config = project.extensions.getByType(Launch4jPluginExtension.class)
        launch4jDependency = project.configurations.named(Launch4jPlugin.LAUNCH4J_CONFIGURATION_NAME_BINARY)
        runtimeClassFiles = project.plugins.hasPlugin('java') ?
            (project.configurations.findByName('runtimeClasspath') ?
                project.configurations.runtimeClasspath : project.configurations.runtime) : project.files()
        ObjectFactory objectFactory = project.objects
        ProjectLayout layout = project.layout
        launch4jBinaryFiles = configurableFileCollection(project)
        launch4jBinaryDirectory = layout.buildDirectory.map {it.file(Launch4jPlugin.LAUNCH4J_BINARY_DIRECTORY)}
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
        def isPropertyConventionSupported = GradleVersion.current() >= GradleVersion.version("5.1")
        if (isPropertyConventionSupported) {
            mainClassName.convention(config.mainClassName)
            jarTask.convention(config.jarTask)
            outputDir.convention(config.outputDir)
            outputDirectory = objectFactory.directoryProperty().convention(layout.buildDirectory.dir(outputDir))
            dontWrapJar.convention(config.dontWrapJar)
            outfile.convention(config.outfile)
            libraryDir.convention(config.libraryDir)
            xmlFileName.convention("${name}.xml")
            headerType.convention(config.headerType)
            errTitle.convention(config.errTitle)
            cmdLine.convention(config.cmdLine)
            chdir.convention(config.chdir)
            priority.convention(config.priority)
            downloadUrl.convention(config.downloadUrl)
            supportUrl.convention(config.supportUrl)
            stayAlive.convention(config.stayAlive)
            restartOnCrash.convention(config.restartOnCrash)
            duplicatesStrategy.convention(config.duplicatesStrategy)
            icon.convention(config.icon)
            version.convention(config.version)
            textVersion.convention(config.textVersion)
            copyright.convention(config.copyright)
            jvmOptions.convention(config.jvmOptions)
            companyName.convention(config.companyName)
            fileDescription.convention(config.fileDescription)
            productName.convention(config.productName)
            internalName.convention(config.internalName)
            trademarks.convention(config.trademarks)
            language.convention(config.language)
            bundledJrePath.convention(config.bundledJrePath)
            requires64Bit.convention(config.requires64Bit)
            jreMinVersion.convention(config.jreMinVersion)
            jreMaxVersion.convention(config.jreMaxVersion)
            requiresJdk.convention(config.requiresJdk)
            variables.convention(config.variables)
            mutexName.convention(config.mutexName)
            windowTitle.convention(config.windowTitle)
            messagesStartupError.convention(config.messagesStartupError)
            messagesJreNotFoundError.convention(config.messagesJreNotFoundError)
            messagesJreVersionError.convention(config.messagesJreVersionError)
            messagesLauncherError.convention(config.messagesLauncherError)
            messagesInstanceAlreadyExists.convention(config.messagesInstanceAlreadyExists)
            initialHeapSize.convention(config.initialHeapSize)
            initialHeapPercent.convention(config.initialHeapPercent)
            maxHeapSize.convention(config.maxHeapSize)
            maxHeapPercent.convention(config.maxHeapPercent)
            splashFileName.convention(config.splashFileName)
            splashWaitForWindows.convention(config.splashWaitForWindows)
            splashTimeout.convention(config.splashTimeout)
            splashTimeoutError.convention(config.splashTimeoutError)
            copyConfigurable.convention(config.copyConfigurable)
            classpath.convention([])
        } else {
            // inputs do not set dependsOn
            dependsOn(config.jarTask)
            mainClassName.set(config.mainClassName)
            jarTask.set(config.jarTask)
            outputDir.set(config.outputDir)
            outputDirectory = layout.directoryProperty()
            outputDirectory.set(layout.buildDirectory.dir(outputDir))
            dontWrapJar.set(config.dontWrapJar)
            outfile.set(config.outfile)
            libraryDir.set(config.libraryDir)
            xmlFileName.set(name + '.xml')
            headerType.set(config.headerType)
            errTitle.set(config.errTitle)
            cmdLine.set(config.cmdLine)
            chdir.set(config.chdir)
            priority.set(config.priority)
            downloadUrl.set(config.downloadUrl)
            supportUrl.set(config.supportUrl)
            stayAlive.set(config.stayAlive)
            restartOnCrash.set(config.restartOnCrash)
            duplicatesStrategy.set(config.duplicatesStrategy)
            icon.set(config.icon)
            version.set(config.version)
            textVersion.set(config.textVersion)
            copyright.set(config.copyright)
            jvmOptions.set(config.jvmOptions)
            companyName.set(config.companyName)
            fileDescription.set(config.fileDescription)
            productName.set(config.productName)
            internalName.set(config.internalName)
            trademarks.set(config.trademarks)
            language.set(config.language)
            bundledJrePath.set(config.bundledJrePath)
            requires64Bit.set(config.requires64Bit)
            jreMinVersion.set(config.jreMinVersion)
            jreMaxVersion.set(config.jreMaxVersion)
            requiresJdk.set(config.requiresJdk)
            variables.set(config.variables)
            mutexName.set(config.mutexName)
            windowTitle.set(config.windowTitle)
            messagesStartupError.set(config.messagesStartupError)
            messagesJreNotFoundError.set(config.messagesJreNotFoundError)
            messagesJreVersionError.set(config.messagesJreVersionError)
            messagesLauncherError.set(config.messagesLauncherError)
            messagesInstanceAlreadyExists.set(config.messagesInstanceAlreadyExists)
            initialHeapSize.set(config.initialHeapSize)
            initialHeapPercent.set(config.initialHeapPercent)
            maxHeapSize.set(config.maxHeapSize)
            maxHeapPercent.set(config.maxHeapPercent)
            splashFileName.set(config.splashFileName)
            splashWaitForWindows.set(config.splashWaitForWindows)
            splashTimeout.set(config.splashTimeout)
            splashTimeoutError.set(config.splashTimeoutError)
            copyConfigurable.set(config.copyConfigurable)
            classpath.set([])
        }
        inputs.files(jarTask.map {it.outputs.files})
        dest = outputDirectory.file(outfile)
        xmlFile = outputDirectory.file(xmlFileName)
        libraryDirectory = outputDirectory.file(libraryDir)
        copyLibraryFileCollection = configurableFileCollection(project)
    }

    @Input
    @Optional
    final Property<String> outputDir

    @OutputDirectory
    final DirectoryProperty outputDirectory

    /**
     * Output executable file<br>
     *
     * Defaults to this task's name appended with '.exe'
     */
    @Optional
    @Input
    final Property<String> outfile

    @OutputFile
    final Provider<RegularFile> dest

    @Input
    @Optional
    final Property<String> xmlFileName

    @Internal
    final Provider<RegularFile> xmlFile

    /**
     * The name where the dependency jars should be copied to.<br>
     *
     * Defaults to <u>lib</u>
     */
    @Input
    @Optional
    final Property<String> libraryDir

    @Internal
    final Provider<RegularFile> libraryDirectory

    final Property<Object> copyConfigurable

    @Input
    @Optional
    def getCopyConfigurable() {
        if(GradleVersion.current() < GradleVersion.version("5.0")) {
            // cannot serialize copyConfigurable
            getCopyFiles()
        } else
            return copyConfigurable
    }
/**
     * Try to get the inputs right.
     * @return the input files of the copyConfigurable
     */
    @InputFiles
    @Optional
    Object getCopyFiles() {
        def copyConfigurable = copyConfigurable.getOrNull()
        if (copyConfigurable instanceof CopySpecInternal) {
            def specResolver = (copyConfigurable as CopySpecInternal).buildRootResolver()
            def files = specResolver.allSource.files
            def rootResolverDestination = specResolver.destPath.getFile(getLibraryDirectory().get().asFile)
            files + rootResolverDestination
            files.addAll((copyConfigurable as DefaultCopySpec).getChildren().collect { CopySpecInternal cpi ->
                cpi.buildRootResolver().destPath.getFile(rootResolverDestination)
            })
            files
        } else if (copyConfigurable instanceof FileCollection) {
            copyConfigurable as FileCollection
        } else {
            null
        }
    }

    private final ConfigurableFileCollection copyLibraryFileCollection

    FileCollection copyLibraries() {
        def jarPath = dontWrapJar.get() ? (getJarTaskOutputPath() ?: getJarTaskDefaultOutputPath()) : null
        new CopyLibraries(config.objectFactory, config.fileOperations, duplicatesStrategy.get()).execute(libraryDirectory.get().asFile, copyConfigurable.getOrNull(), jarPath, runtimeClassFiles, copyLibraryFileCollection)
    }

    static ConfigurableFileCollection configurableFileCollection(Project project) {
        if (GradleVersion.current() >= GradleVersion.version("5.3")) {
            project.objects.fileCollection()
        } else {
            project.files()
        }
    }

    /**
     * The main class to start if {@link #jarTask} is not set.
     */
    @Input
    @Optional
    final Property<String> mainClassName

    @Internal
    final Property<Task> jarTask

    @Internal
    @Override
    Path getJarTaskOutputPath() {
        jarTask.getOrNull()?.outputs?.files?.singleFile?.toPath()
    }

    @Internal
    @Override
    Path getJarTaskDefaultOutputPath() {
        config.getJarTaskDefaultOutputPath()
    }

    /**
     * Optional, defaults to <u>false</u>.<br>
     *     Launch4j by default wraps jars in native executables, you can prevent this by setting {@link #dontWrapJar} to true.
     *     The exe acts then as a launcher and starts the application specified in {@link #jarTask} or this runtime dependencies and {@link #mainClassName}
     */
    @Input
    @Optional
    final Property<Boolean> dontWrapJar

    /**
     * Type of the header used to wrap the application.
     * <br>
     * Must be one of <u>gui</u>, console, jniGui32, jniConsole32
     */
    @Input
    @Optional
    final Property<String> headerType

    /**
     * Optional, sets the title of the error message box that's displayed if Java cannot be found for instance.
     * This usually should contain the name of your application. The console header prefixes error messages with this property (myapp: error...)
     */
    @Input
    @Optional
    final Property<String> errTitle

    /**
     * Optional, constant command line arguments.
     */
    @Input
    @Optional
    final Property<String> cmdLine

    /**
     * Optional. Change current directory to an arbitrary path relative to the executable.
     * If you omit this property or leave it blank it will have no effect.
     * Setting it to {@code .} will change the current dir to the same directory as the executable. {@code ..} will change it to the parent directory, and so on.
     */
    @Input
    @Optional
    final Property<String> chdir

    /**
     * The process priority.<br>
     * Can be <u>{@code normal}</u>, {@code idle} or {@code high}.
     */
    @Input
    @Optional
    final Property<String> priority

    /**
     * The page to navigate the browser to if no java environment is found.<br>
     *
     * Defaults to <u>http://java.com/download</u>
     */
    @Input
    @Optional
    final Property<String> downloadUrl

    @Input
    @Optional
    final Property<String> supportUrl

    /**
     * Optional, defaults to <u>false</u> in GUI header, always true in console header. When enabled the launcher waits for the Java application to finish and returns it's exit code.
     */
    @Input
    @Optional
    final Property<Boolean> stayAlive

    /**
     * The duplication Strategy to use if duplicates are found.
     *
     * Defaults to DuplicatesStrategy.EXCLUDE
     */
    @Input
    @Optional
    final Property<DuplicatesStrategy> duplicatesStrategy

    /**
     * Optional, defaults to <u>false</u>.
     * When the application exits, any exit code other than 0 is considered a crash and the application will be started again.
     */
    @Input
    @Optional
    final Property<Boolean> restartOnCrash

    /**
     * Application icon in ICO format. May contain multiple color depths/resolutions.
     */
    @Input
    @Optional
    final Property<String> icon

    /**
     * Version number {@code 'x.x.x.x'}
     */
    @Input
    @Optional
    final Property<String> version

    /**
     * Free form file version, for example {@code '1.20.RC1'}.
     */
    @Input
    @Optional
    final Property<String> textVersion

    /**
     * Legal copyright.
     */
    @Input
    @Optional
    final Property<String> copyright

    /**
     * Optional, accepts everything you would normally pass to java/javaw launcher: assertion options, system properties and X options. Here you can map environment and special variables <i>EXEDIR</i> (exe's runtime directory), <i>EXEFILE</i> (exe's runtime full file path) to system properties. All variable references must be surrounded with percentage signs and quoted.
     * <ul>
     *   <li>-Dlaunch4j.exedir="%EXEDIR%"</li>
     *   <li>-Dlaunch4j.exefile="%EXEFILE%"</li>
     *   <li>-Denv.path="%Path%"</li>
     *   <li>-Dsettings="%HomeDrive%%HomePath%\\settings.ini"</li>
     * </ul>
     */
    @Input
    @Optional
    final SetProperty<String> jvmOptions

    @Input
    @Optional
    final SetProperty<String> variables
    /**
     * Optional text.
     */
    @Input
    @Optional
    final Property<String> companyName

    /**
     * File description presented to the user.
     */
    @Input
    @Optional
    final Property<String> fileDescription

    /**
     * Text.
     */
    @Input
    @Optional
    final Property<String> productName

    /**
     * Internal name without extension, original filename or module name for example.
     */
    @Input
    @Optional
    final Property<String> internalName

    /**
     * Trademarks that apply to the file.
     */
    @Input
    @Optional
    final Property<String> trademarks

    /**
     * One of the language codes.
     */
    @Input
    @Optional
    final Property<String> language

    /**
     * The {@code bundledJrePath} property is used to specify absolute or relative JRE paths, it does not rely on the current directory or {@link #chdir}. Note that this path is not checked until the actual application execution.
     *
     * {@code bundledJrePath} is now required and always used for searching before the registry in order to ensure compatibility with latest runtimes which by default do not add registry keys during installation.
     * {@link #jreMinVersion} and {@link #jreMaxVersion} are now considered during path and registry search, previously the version was checked only during registry search. The first runtime version matching the given range will be used.
     *
     * @see #jreMinVersion
     * @see #jreMaxVersion
     */
    @Input
    @Optional
    final Property<String> bundledJrePath

    @Deprecated
    @ReplacedBy("requires64Bit")
    Boolean getBundledJre64Bit() {
        logger.warn("use requires64Bit instead of bundledJre64Bit")
        requires64Bit.getOrElse(false)
    }

    @Deprecated
    void setBundledJre64Bit(Boolean value) {
        logger.warn("use requires64Bit instead of bundledJre64Bit")
        requires64Bit.set(value)
    }

    /**
     * Optional, defaults to <u>false</u>. True limits the runtimes to 64-Bit only, false will use 64-Bit or 32-Bit depending on which is found.
     *
     * This option works with path and registry search.
     */
    @Input
    @Optional
    final Property<Boolean> requires64Bit

    /**
     * The minimum Java version
     *
     * The traditional version scheme supported by launch4j is {@code 1.x.x[_xxx]} and requires at least 3 parts, for example:
     * <pre>
     * 1.6.0
     * 1.7.0_51
     * 1.8.0_121
     * </pre>
     *
     * The new version format for Java >= 9 is {@code xxx[.xxx[.xxx]]} where only 1 part is required and the update version after the underscore is not allowed.
     * <pre>
     * 1.6
     * 9
     * 10.0.1
     * </pre>
     *
     * @see #jreMaxVersion
     * @see #bundledJrePath
     */
    @Input
    @Optional
    final Property<String> jreMinVersion

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion.isPresent()) {
            config.internalJreMinVersion()
        } else {
            jreMinVersion.get()
        }
    }

    /**
     * The maximum Java version
     *
     * The traditional version scheme supported by launch4j is {@code 1.x.x[_xxx]} and requires at least 3 parts, for example:
     * <pre>
     * 1.6.0
     * 1.7.0_51
     * 1.8.0_121
     * </pre>
     *
     * The new version format for Java >= 9 is {@code xxx[.xxx[.xxx]]} where only 1 part is required and the update version after the underscore is not allowed.
     * <pre>
     * 1.6
     * 9
     * 10.0.1
     * </pre>
     *
     * @see #jreMinVersion
     * @see #bundledJrePath
     */
    @Input
    @Optional
    final Property<String> jreMaxVersion

    /**
     * Optional, defaults to <u>false</u>. When true only a JDK will be used for execution.
     * An additional check will be performed if javac is available during path and registry search.
     *
     */
    @Input
    @Optional
    final Property<Boolean> requiresJdk

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

    /**
     * Unique mutex name that will identify the application.
     */
    @Input
    @Optional
    final Property<String> mutexName


    /**
     * Optional, recognized by GUI header only. Title or title part of a window to bring up instead of running a new instance.
     */
    @Input
    @Optional
    final Property<String> windowTitle


    @Input
    @Optional
    final Property<String> messagesStartupError


    @Input
    @Optional
    final Property<String> messagesJreNotFoundError

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

    @Input
    @Optional
    final Property<String> messagesJreVersionError


    @Input
    @Optional
    final Property<String> messagesLauncherError


    @Input
    @Optional
    final Property<String> messagesInstanceAlreadyExists


    /**
     * Optional, initial heap size in MB.<br>
     *
     * If {@link #initialHeapSize} and {@link #initialHeapPercent} are specified, then the setting which yields more memory will be chosen at runtime. In other words, setting both values means: percent of available memory no less than size in MB.
     *
     * If the runtime is 32-Bit then a 32-Bit limit will be imposed even if more memory is available during path and registry search.
     *
     * @see DefaultLaunch4jTask#initialHeapPercent
     * @see DefaultLaunch4jTask#maxHeapSize
     * @see DefaultLaunch4jTask#maxHeapPercent
     */
    @Input
    @Optional
    final Property<Integer> initialHeapSize


    /**
     * Optional, initial heap size in % of available memory.<br>
     *
     * If {@link #initialHeapSize} and {@link #initialHeapPercent} are specified, then the setting which yields more memory will be chosen at runtime. In other words, setting both values means: percent of available memory no less than size in MB.
     *
     * If the runtime is 32-Bit then a 32-Bit limit will be imposed even if more memory is available during path and registry search.
     *
     * @see DefaultLaunch4jTask#initialHeapSize
     * @see DefaultLaunch4jTask#maxHeapSize
     * @see DefaultLaunch4jTask#maxHeapPercent
     */
    @Input
    @Optional
    final Property<Integer> initialHeapPercent


    /**
     * Optional, max heap size in MB.<br>
     *
     * If {@link #maxHeapSize} and {@link #maxHeapPercent} are specified, then the setting which yields more memory will be chosen at runtime. In other words, setting both values means: percent of available memory no less than size in MB.
     *
     * If the runtime is 32-Bit then a 32-Bit limit will be imposed even if more memory is available during path and registry search.
     *
     * @see DefaultLaunch4jTask#initialHeapSize
     * @see DefaultLaunch4jTask#initialHeapPercent
     * @see DefaultLaunch4jTask#maxHeapPercent
     */
    @Input
    @Optional
    final Property<Integer> maxHeapSize


    /**
     * Optional, max heap size in % of available memory.<br>
     *
     * If {@link #maxHeapSize} and {@link #maxHeapPercent} are specified, then the setting which yields more memory will be chosen at runtime. In other words, setting both values means: percent of available memory no less than size in MB.
     *
     * If the runtime is 32-Bit then a 32-Bit limit will be imposed even if more memory is available during path and registry search.
     *
     * @see DefaultLaunch4jTask#initialHeapSize
     * @see DefaultLaunch4jTask#initialHeapPercent
     * @see DefaultLaunch4jTask#maxHeapSize
     */
    @Input
    @Optional
    final Property<Integer> maxHeapPercent

    /**
     * Splash screen image in BMP format.
     */
    @Input
    @Optional
    final Property<String> splashFileName


    /**
     * Optional, defaults to <u>true</u>. Close the splash screen when an application window or Java error message box appears. If set to false, the splash screen will be closed on timeout.
     */
    @Input
    @Optional
    final Property<Boolean> splashWaitForWindows

    /**
     * Optional, defaults to <u>60</u>. Number of seconds after which the splash screen must be closed. Splash timeout may cause an error depending on {@link #splashTimeoutError}.
     */
    @Input
    @Optional
    final Property<Integer> splashTimeout


    /**
     * Optional, defaults to <u>true</u>. True signals an error on splash timeout, false closes the splash screen quietly.
     */
    @Input
    @Optional
    final Property<Boolean> splashTimeoutError

    @Input
    @Optional
    final SetProperty<String> classpath

    protected void createXML(FileCollection copySpec) {
        new CreateXML().execute(xmlFile.get().asFile, this, copySpec, runtimeClassFiles)
    }

    protected void createExecutableFolder() {
        dest.get().asFile.parentFile?.mkdirs()
    }
}
