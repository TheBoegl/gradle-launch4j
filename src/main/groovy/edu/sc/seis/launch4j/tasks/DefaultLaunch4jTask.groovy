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

package edu.sc.seis.launch4j.tasks


import edu.sc.seis.launch4j.CopyLibraries
import edu.sc.seis.launch4j.CreateXML
import edu.sc.seis.launch4j.Launch4jConfiguration
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import org.gradle.util.GradleVersion

import java.nio.file.Path
// do not compile static because this will break the layout#directoryProperty() for gradle version 4.3 to 5.1.
abstract class DefaultLaunch4jTask extends DefaultTask implements Launch4jConfiguration {

    private Launch4jPluginExtension config

    protected DefaultLaunch4jTask() {
        config = project.extensions.getByType(Launch4jPluginExtension.class)
        if (GradleVersion.current() >= GradleVersion.version("4.4")) {
            ObjectFactory objectFactory = project.objects
            ProjectLayout layout = project.layout
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
            if (GradleVersion.current() >= GradleVersion.version("5.1")) {
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
                manifest.convention(config.manifest)
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
                bundledJre64Bit.convention(config.bundledJre64Bit)
                bundledJreAsFallback.convention(config.bundledJreAsFallback)
                jreMinVersion.convention(config.jreMinVersion)
                jreMaxVersion.convention(config.jreMaxVersion)
                jdkPreference.convention(config.jdkPreference)
                jreRuntimeBits.convention(config.jreRuntimeBits)
                variables.convention(config.variables)
                mutexName.convention(config.mutexName)
                windowTitle.convention(config.windowTitle)
                messagesStartupError.convention(config.messagesStartupError)
                messagesBundledJreError.convention(config.messagesBundledJreError)
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
                xmlFileName.set("${name}.xml")
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
                manifest.set(config.manifest)
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
                bundledJre64Bit.set(config.bundledJre64Bit)
                bundledJreAsFallback.set(config.bundledJreAsFallback)
                jreMinVersion.set(config.jreMinVersion)
                jreMaxVersion.set(config.jreMaxVersion)
                jdkPreference.set(config.jdkPreference)
                jreRuntimeBits.set(config.jreRuntimeBits)
                variables.set(config.variables)
                mutexName.set(config.mutexName)
                windowTitle.set(config.windowTitle)
                messagesStartupError.set(config.messagesStartupError)
                messagesBundledJreError.set(config.messagesBundledJreError)
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
        } else {
            throw new IllegalStateException("at least gradle 4.4 is required for this plugin to work and provide org.gradle.api.provider.Property")
        }
    }

    @Input
    @Optional
    final Property<String> outputDir

    @OutputDirectory
    final DirectoryProperty outputDirectory

    private File getOutputDirectoryAsFile() {
        outputDirectory.get().getAsFile();
    }

    /**
     * Output executable file<br>
     *
     * Defaults to this task's name appended with '.exe'
     */
    @Optional
    @Input
    final Property<String> outfile

    @Override
    @OutputFile
    File getDest() {
        project.file("${getOutputDirectoryAsFile()}/${outfile.get()}")
    }

    @Input
    final Property<String> xmlFileName

    @Override
    File getXmlFile() {
        project.file("${getOutputDirectoryAsFile()}/${xmlFileName.get()}")
    }

    /**
     * The name where the dependency jars should be copied to.<br>
     *
     * Defaults to <u>lib</u>
     */
    @Input
    @Optional
    final Property<String> libraryDir

    @Internal
    File getLibraryDirectory() {
        project.file("${getOutputDirectoryAsFile()}/${libraryDir.get()}")
    }

    @Input
    @Optional
    final Property<Object> copyConfigurable

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
            def rootResolverDestination = specResolver.destPath.getFile(getLibraryDirectory())
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

    FileCollection copyLibraries() {
        def jarPath = getDontWrapJar() ? (getJarTaskOutputPath() ?: getJarTaskDefaultOutputPath()) : null
        new CopyLibraries(project, config.fileOperations, duplicatesStrategy.get()).execute(getLibraryDirectory(), getCopyConfigurable().getOrNull(), jarPath)
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

    @Input
    @Optional
    final Property<String> manifest

    /**
     * Application icon in ICO format. May contain multiple color depths/resolutions.
     */
    @Input
    @Optional
    final Property<String> icon

    /**
     * Version number 'x.x.x.x'
     */
    @Input
    @Optional
    final Property<String> version

    /**
     * Free form file version, for example '1.20.RC1'.
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
     * Run if bundled JRE and javaw.exe are present, otherwise stop with error
     */
    @Input
    @Optional
    final Property<String> bundledJrePath

    /**
     * Optional, defaults to <u>false</u> which limits the calculated heap size to the 32-bit maximum. Set to true in order to use the available memory without this limit. This option works in combination with the HeapSize and HeapPercent options only if the found JRE is a bundled one. In the standard JRE search based on registry the wrapper detects the type of JRE itself and uses the 32-bit heap limit when needed.
     */
    @Input
    @Optional
    final Property<Boolean> bundledJre64Bit

    /**
     * Optional, defaults to <u>false</u> which treats the bundled JRE as the primary runtime. When set to true, the bundled JRE will only be used in case the mix/max version search fails. This can be used as a fallback option if the user does not have the required Java installed and the bundled JRE is provided on a CD or shared network location.
     */
    @Input
    @Optional
    final Property<Boolean> bundledJreAsFallback

    /**
     * If {@link #bundledJrePath} is set:
     * <ul><li>Search for Java, if an appropriate version cannot be found display error message and open the Java download page.</li</ul>
     * Else:
     * <ul><li>Use bundled JRE first, if it cannot be located search for Java, if that fails display error message and open the Java download page. </li></ul>
     * @see DefaultLaunch4jTask#jreMaxVersion
     */
    @Input
    @Optional
    final Property<String> jreMinVersion

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion.isPresent()) {
            config.internalJreMinVersion()
        }
        jreMinVersion
    }

    /**
     * If {@link #bundledJrePath} is set:
     * <ul><li>Search for Java, if an appropriate version cannot be found display error message and open the Java download page.</li</ul>
     * Else:
     * <ul><li>Use bundled JRE first, if it cannot be located search for Java, if that fails display error message and open the Java download page. </li></ul>
     * @see DefaultLaunch4jTask#jreMinVersion
     */
    @Input
    @Optional
    final Property<String> jreMaxVersion

    /**
     * Optional, defaults to preferJre; Allows you to specify a preference for a public JRE or a private JDK runtime. Valid values are:
     * <ul>
     *     <li><strong>jreOnly</strong><br>Always use a public JRE (equivalent to the old option dontUsePrivateJres=true)</li>
     *     <li><strong><u>preferJre</u></strong><br>Prefer a public JRE, but use a JDK private runtime if it is newer than the public JRE (equivalent to the old option dontUsePrivateJres=false)</li>
     *     <li><strong>preferJdk</strong><br>Prefer a JDK private runtime, but use a public JRE if it is newer than the JDK </li>
     *     <li><strong>jdkOnly</strong><br>Always use a private JDK runtime (fails if there is no JDK installed)</li>
     * </ul>
     *
     */
    @Input
    @Optional
    final Property<String> jdkPreference


    /**
     * Optional, defaults to 64/32; Allows to select between 64-bit and 32-bit runtimes. Valid values are:
     * <ul>
     *     <li><strong>64</strong><br>Use only 64-bit runtimes</li>
     *     <li><strong><u>64/32</u></strong><br>Use 64-bit runtimes if available, otherwise use 32-bit</li>
     *     <li><strong>32/64</strong><br>Use 32-bit runtimes if available, otherwise use 64-bit</li>
     *     <li><strong>32</strong><br>Use only 32-bit runtimes</li>
     * </ul>
     *
     */
    @Input
    @Optional
    final Property<String> jreRuntimeBits

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
    final Property<String> messagesBundledJreError


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
        new CreateXML(project).execute(getXmlFile(), this, copySpec)
    }

    protected void createExecutableFolder() {
        getDest().parentFile?.mkdirs()
    }
}
