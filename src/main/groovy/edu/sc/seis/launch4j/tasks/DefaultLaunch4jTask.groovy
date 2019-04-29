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

import edu.sc.seis.launch4j.*
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.CopySpecInternal
import org.gradle.api.internal.file.copy.DefaultCopySpec
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.Jar

//@CompileStatic // bug #34: do not compile static because this will break the #getInputs() for gradle version < 3.
abstract class DefaultLaunch4jTask extends DefaultTask implements Launch4jConfiguration {

    private Launch4jPluginExtension config

    protected DefaultLaunch4jTask() {
        config = project.getConvention().getByType(Launch4jPluginExtension.class)
        evaluateTaskDependencyIfAvailable('shadowJar', 'fatJar', 'jar')
    }

    private void evaluateTaskDependencyIfAvailable(String... taskNames) {
        project.afterEvaluate {
            for (String taskName : taskNames) {
                if (project.hasProperty(taskName)) {
                    def task = project.tasks.getByName(taskName)
                    dependsOn.add(task)
                    inputs.files(task.outputs.files)
                    break
                }
            }
        }
    }

    @Input
    @Optional
    String outputDir

    @Override
    String getOutputDir() {
        outputDir ?: config.outputDir
    }

    @Override
    @OutputDirectory
    File getOutputDirectory() {
        project.file("${project.buildDir}/${outputDir ?: config.outputDir}")
    }

    /**
     * Output executable file<br>
     *
     * Defaults to this task's name appended with '.exe'
     */
    @Input
    String outfile = "${name}.exe"

    @Override
    String getOutfile() {
        outfile ? (outfile == (Launch4jPlugin.TASK_RUN_NAME + '.exe') ? config.outfile : outfile) : config.outfile
    }

    @Override
    File getDest() {
        project.file("${getOutputDirectory()}/${getOutfile()}")
    }

    @Input
    String xmlFileName = "${name}.xml"

    @Override
    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName ?: config.xmlFileName}")
    }

    /**
     * The name where the dependency jars should be copied to.<br>
     *
     * Defaults to <u>lib</u>
     */
    @Input
    @Optional
    String libraryDir

    @Override
    String getLibraryDir() {
        libraryDir ?: config.libraryDir
    }

    @OutputDirectory
    File getLibraryDirectory() {
        project.file("${getOutputDirectory()}/${libraryDir ?: config.libraryDir}")
    }

    Object copyConfigurable

    /**
     * Try to get the inputs right.
     * @return the input files of the copyConfigurable
     */
    @InputFiles
    @Optional
    Object getCopyFiles() {
        def copyConfigurable = getCopyConfigurable()
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

    private Object getCopyConfigurable() {
        copyConfigurable ?: config.copyConfigurable
    }

    FileCollection copyLibraries() {
        new CopyLibraries(project, config.fileOperations).execute(getLibraryDirectory(), getCopyConfigurable())
    }

    /**
     * The main class to start if {@link #jar} is not set.
     */
    @Input
    @Optional
    String mainClassName

    @Override
    String getMainClassName() {
        mainClassName ?: config.mainClassName
    }

    /**
     * Optional, by default specifies the jar to wrap.
     * To launch a jar without wrapping it enter the runtime path of the jar relative to the executable and set {@link #dontWrapJar} to true.
     * For example, if the executable launcher and the application jar named <i>calc.exe</i> and <i>calc.jar</i> are in the same directory then you would use {@code jar = calc.jar} and {@code dontWrapJar = true}.
     */
    @Input
    @Optional
    String jar

    @Override
    String getJar() {
        jar ? internalJar() : config.internalJar()
    }

    /**
     * Optional, defaults to <u>false</u>.<br>
     *     Launch4j by default wraps jars in native executables, you can prevent this by setting {@link #dontWrapJar} to true.
     *     The exe acts then as a launcher and starts the application specified in {@link #jar} or this runtime dependencies and {@link #mainClassName}
     */
    @Input
    @Optional
    Boolean dontWrapJar

    @Override
    Boolean getDontWrapJar() {
        if (dontWrapJar == null) {
            return config.getDontWrapJar()
        }
        return dontWrapJar
    }

    /**
     * Type of the header used to wrap the application.
     * <br>
     * Must be one of <u>gui</u>, console, jniGui32, jniConsole32
     */
    @Input
    @Optional
    String headerType

    @Override
    String getHeaderType() {
        headerType ?: config.headerType
    }

    /**
     * Optional, sets the title of the error message box that's displayed if Java cannot be found for instance.
     * This usually should contain the name of your application. The console header prefixes error messages with this property (myapp: error...)
     */
    @Input
    @Optional
    String errTitle

    @Override
    String getErrTitle() {
        errTitle ?: config.errTitle
    }

    /**
     * Optional, constant command line arguments.
     */
    @Input
    @Optional
    String cmdLine

    @Override
    String getCmdLine() {
        cmdLine ?: config.cmdLine
    }

    /**
     * Optional. Change current directory to an arbitrary path relative to the executable.
     * If you omit this property or leave it blank it will have no effect.
     * Setting it to {@code .} will change the current dir to the same directory as the executable. {@code ..} will change it to the parent directory, and so on.
     */
    @Input
    @Optional
    String chdir

    @Override
    String getChdir() {
        (chdir != null) ? chdir : config.chdir
    }

    /**
     * The process priority.<br>
     * Can be <u>{@code normal}</u>, {@code idle} or {@code high}.
     */
    @Input
    @Optional
    String priority

    @Override
    String getPriority() {
        priority ?: config.priority
    }

    /**
     * The page to navigate the browser to if no java environment is found.<br>
     *
     * Defaults to <u>http://java.com/download</u>
     */
    @Input
    @Optional
    String downloadUrl

    @Override
    String getDownloadUrl() {
        downloadUrl ?: config.downloadUrl
    }

    @Input
    @Optional
    String supportUrl

    @Override
    String getSupportUrl() {
        supportUrl ?: config.supportUrl
    }

    /**
     * Optional, defaults to <u>false</u> in GUI header, always true in console header. When enabled the launcher waits for the Java application to finish and returns it's exit code.
     */
    @Input
    @Optional
    Boolean stayAlive

    @Override
    Boolean getStayAlive() {
        if (stayAlive == null) {
            return config.stayAlive
        }
        return stayAlive
    }

    /**
     * Optional, defaults to <u>false</u>.
     * When the application exits, any exit code other than 0 is considered a crash and the application will be started again.
     */
    @Input
    @Optional
    Boolean restartOnCrash

    @Override
    Boolean getRestartOnCrash() {
        if (restartOnCrash == null) {
            return config.restartOnCrash
        }
        return restartOnCrash
    }

    @Input
    @Optional
    String manifest

    @Override
    String getManifest() {
        manifest ?: config.manifest
    }

    /**
     * Application icon in ICO format. May contain multiple color depths/resolutions.
     */
    @Input
    @Optional
    String icon

    @Override
    String getIcon() {
        icon ?: config.icon
    }

    /**
     * Version number 'x.x.x.x'
     */
    @Input
    @Optional
    String version

    @Override
    String getVersion() {
        version ?: config.version
    }

    /**
     * Free form file version, for example '1.20.RC1'.
     */
    @Input
    @Optional
    String textVersion

    @Override
    String getTextVersion() {
        textVersion ?: config.textVersion
    }

    /**
     * Legal copyright.
     */
    @Input
    @Optional
    String copyright

    @Override
    String getCopyright() {
        copyright ?: config.copyright
    }

    /**
     * Optional, accepts everything you would normally pass to java/javaw launcher: assertion options, system properties and X options. Here you can map environment and special variables <i>EXEDIR</i> (exe's runtime directory), <i>EXEFILE</i> (exe's runtime full file path) to system properties. All variable references must be surrounded with percentage signs and quoted.
     */
    @Input
    @Optional
    Set<String> jvmOptions = []

    @Override
    Set<String> getJvmOptions() {
        jvmOptions ?: config.jvmOptions
    }

    @Deprecated
    void setOpt(String opt) {
        if (!opt) return // null check
        this.jvmOptions = [opt] as Set
        project.logger.warn("${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.opt property is deprecated. Use ${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.jvmOptions instead.")
    }

    @Input
    @Optional
    Set<String> variables = []

    @Override
    Set<String> getVariables() {
        variables ?: config.variables
    }
    /**
     * Optional text.
     */
    @Input
    @Optional
    String companyName

    @Override
    String getCompanyName() {
        companyName ?: config.companyName
    }

    /**
     * File description presented to the user.
     */
    @Input
    @Optional
    String fileDescription

    @Override
    String getFileDescription() {
        fileDescription ?: config.fileDescription
    }

    /**
     * Text.
     */
    @Input
    @Optional
    String productName

    @Override
    String getProductName() {
        productName ?: config.productName
    }

    /**
     * Internal name without extension, original filename or module name for example.
     */
    @Input
    @Optional
    String internalName

    @Override
    String getInternalName() {
        internalName ?: config.internalName
    }

    /**
     * Trademarks that apply to the file.
     */
    @Input
    @Optional
    String trademarks

    @Override
    String getTrademarks() {
        trademarks ?: config.trademarks
    }

    /**
     * One of the language codes.
     */
    @Input
    @Optional
    String language

    @Override
    String getLanguage() {
        language ?: config.language
    }
    /**
     * Run if bundled JRE and javaw.exe are present, otherwise stop with error
     */
    @Input
    @Optional
    String bundledJrePath

    @Override
    String getBundledJrePath() {
        bundledJrePath ?: config.bundledJrePath
    }

    /**
     * Optional, defaults to <u>false</u> which limits the calculated heap size to the 32-bit maximum. Set to true in order to use the available memory without this limit. This option works in combination with the HeapSize and HeapPercent options only if the found JRE is a bundled one. In the standard JRE search based on registry the wrapper detects the type of JRE itself and uses the 32-bit heap limit when needed.
     */
    @Input
    @Optional
    Boolean bundledJre64Bit

    @Override
    Boolean getBundledJre64Bit() {
        if (bundledJre64Bit == null) {
            return config.bundledJre64Bit
        }
        return bundledJre64Bit
    }

    /**
     * Optional, defaults to <u>false</u> which treats the bundled JRE as the primary runtime. When set to true, the bundled JRE will only be used in case the mix/max version search fails. This can be used as a fallback option if the user does not have the required Java installed and the bundled JRE is provided on a CD or shared network location.
     */
    @Input
    @Optional
    Boolean bundledJreAsFallback

    @Override
    Boolean getBundledJreAsFallback() {
        if (bundledJreAsFallback == null) {
            return config.bundledJreAsFallback
        }
        return bundledJreAsFallback
    }
    /**
     * If {@link #bundledJrePath} is set:
     * <ul><li>Search for Java, if an appropriate version cannot be found display error message and open the Java download page.</li</ul>
     * Else:
     * <ul><li>Use bundled JRE first, if it cannot be located search for Java, if that fails display error message and open the Java download page. </li></ul>
     * @see DefaultLaunch4jTask#jreMaxVersion
     */
    @Input
    @Optional
    String jreMinVersion

    @Override
    String getJreMinVersion() {
        if (jreMinVersion != null) {
            return jreMinVersion
        }
        return config.jreMinVersion
    }

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

    /**
     * If {@link #bundledJrePath} is set:
     * <ul><li>Search for Java, if an appropriate version cannot be found display error message and open the Java download page.</li</ul>
     * Else:
     * <ul><li>Use bundled JRE first, if it cannot be located search for Java, if that fails display error message and open the Java download page. </li></ul>
     * @see DefaultLaunch4jTask#jreMinVersion
     */
    @Input
    @Optional
    String jreMaxVersion

    @Override
    String getJreMaxVersion() {
        jreMaxVersion ?: config.jreMaxVersion
    }

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
    String jdkPreference

    @Override
    String getJdkPreference() {
        jdkPreference ?: config.jdkPreference
    }

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
    String jreRuntimeBits

    @Override
    String getJreRuntimeBits() {
        jreRuntimeBits ?: config.jreRuntimeBits
    }
    /**
     * Unique mutex name that will identify the application.
     */
    @Input
    @Optional
    String mutexName

    @Override
    String getMutexName() {
        mutexName ?: config.mutexName
    }

    /**
     * Optional, recognized by GUI header only. Title or title part of a window to bring up instead of running a new instance.
     */
    @Input
    @Optional
    String windowTitle

    @Override
    String getWindowTitle() {
        windowTitle ?: config.windowTitle
    }

    @Input
    @Optional
    String messagesStartupError

    @Override
    String getMessagesStartupError() {
        messagesStartupError ?: config.messagesStartupError
    }

    @Input
    @Optional
    String messagesBundledJreError

    @Override
    String getMessagesBundledJreError() {
        messagesBundledJreError ?: config.messagesBundledJreError
    }

    @Input
    @Optional
    String messagesJreVersionError

    @Override
    String getMessagesJreVersionError() {
        messagesJreVersionError ?: config.messagesJreVersionError
    }

    @Input
    @Optional
    String messagesLauncherError

    @Override
    String getMessagesLauncherError() {
        messagesLauncherError ?: config.messagesLauncherError
    }

    @Input
    @Optional
    String messagesInstanceAlreadyExists

    @Override
    String getMessagesInstanceAlreadyExists() {
        messagesInstanceAlreadyExists ?: config.messagesInstanceAlreadyExists
    }

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
    Integer initialHeapSize

    @Override
    Integer getInitialHeapSize() {
        initialHeapSize ?: config.initialHeapSize
    }

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
    Integer initialHeapPercent

    @Override
    Integer getInitialHeapPercent() {
        initialHeapPercent ?: config.initialHeapPercent
    }

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
    Integer maxHeapSize

    @Override
    Integer getMaxHeapSize() {
        maxHeapSize ?: config.maxHeapSize
    }

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
    Integer maxHeapPercent

    @Override
    Integer getMaxHeapPercent() {
        maxHeapPercent ?: config.maxHeapPercent
    }
    /**
     * Splash screen image in BMP format.
     */
    @Input
    @Optional
    String splashFileName

    @Override
    String getSplashFileName() {
        splashFileName ?: config.splashFileName
    }

    /**
     * Optional, defaults to <u>true</u>. Close the splash screen when an application window or Java error message box appears. If set to false, the splash screen will be closed on timeout.
     */
    @Input
    @Optional
    Boolean splashWaitForWindows

    @Override
    Boolean getSplashWaitForWindows() {
        if (splashWaitForWindows == null) {
            return config.splashWaitForWindows
        }
        return splashWaitForWindows
    }

    /**
     * Optional, defaults to <u>60</u>. Number of seconds after which the splash screen must be closed. Splash timeout may cause an error depending on {@link #splashTimeoutError}.
     */
    @Input
    @Optional
    Integer splashTimeout

    @Override
    Integer getSplashTimeout() {
        splashTimeout ?: config.splashTimeout
    }

    /**
     * Optional, defaults to <u>true</u>. True signals an error on splash timeout, false closes the splash screen quietly.
     */
    @Input
    @Optional
    Boolean splashTimeoutError

    @Override
    Boolean getSplashTimeoutError() {
        if (splashTimeoutError == null) {
            return config.splashTimeoutError
        }
        return splashTimeoutError
    }

    String internalJar() {
        if (!jar) {
            if (project.plugins.hasPlugin('java')) {
                def jarTask = project.tasks[JavaPlugin.JAR_TASK_NAME] as Jar
                jar = "${libraryDir}/${jarTask.archiveName}"
            } else {
                jar = ""
            }
        }
        jar
    }

    @Input
    @Optional
    Set<String> classpath = []

    @Override
    Set<String> getClasspath() {
        classpath ?: config.classpath
    }

    protected void createXML(FileCollection copySpec) {
        new CreateXML(project).execute(getXmlFile(), this, copySpec)
    }

    protected void createExecutableFolder() {
        getDest().parentFile?.mkdirs()
    }

}
