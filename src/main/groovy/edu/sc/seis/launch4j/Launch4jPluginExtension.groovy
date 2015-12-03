package edu.sc.seis.launch4j
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.listener.ActionBroadcast

import java.nio.file.Paths

class Launch4jPluginExtension implements Serializable {

    String launch4jCmd = "launch4j"
    boolean externalLaunch4j = false
    String outputDir
	String libraryDir = "lib"
    String xmlFileName = "launch4j.xml"
    String mainClassName
    boolean dontWrapJar = false
    String headerType = "gui"
    String jar
    String outfile
    String errTitle = ""
    String cmdLine = ""
    String chdir = '.'
    String priority = 'normal'
    String downloadUrl = "http://java.com/download"
    String supportUrl = ""
    boolean stayAlive = false
    boolean restartOnCrash = false
    String manifest = ""
    String icon = ""
    String version = ""
    String textVersion = ""
    String copyright = "unknown"
    String opt = ""
    String companyName = ""
    String description
    String productName
    String internalName

    String bundledJrePath
    boolean bundledJre64Bit = false
    boolean bundledJreAsFallback = false
    String jreMinVersion
    String jreMaxVersion
    String jdkPreference = "preferJre"
    String jreRuntimeBits = "64/32"

    String mutexName
    String windowTitle

    String messagesStartupError
    String messagesBundledJreError
    String messagesJreVersionError
    String messagesLauncherError

    Integer initialHeapSize
    Integer initialHeapPercent
    Integer maxHeapSize
    Integer maxHeapPercent

    String splashFileName
    boolean splashWaitForWindows = true
    Integer splashTimeout = 60
    boolean splashTimeoutError = true

    private transient Object copyConfigurable

    private transient ActionBroadcast<Object> onSetCopyConfigurable = new ActionBroadcast<>()

    void afterEvaluate(Project project) {
        if (!outfile) {
            outfile = "${project.name}.exe"
        }
        if (!outputDir) {
            outputDir = "${project.buildDir}/launch4j"
        }
        project.mkdir(outputDir)
        project.mkdir(Paths.get(outputDir, outfile).parent.toString())
        // initialize the jar variable with a default value later
        if (!version) {
            version = project.version
        }
        if (!textVersion) {
            textVersion = project.version
        }
        if (!description) {
            description = project.name
        }
        if (!productName) {
            productName = project.name
        }
        if (!internalName) {
            internalName = project.name
        }
        if (!jreMinVersion) {
            if (project.hasProperty("targetCompatibility")) {
                jreMinVersion = project.targetCompatibility
            } else {
                jreMinVersion = JavaVersion.current()
            }
            while (jreMinVersion ==~ /\d+(\.\d+)?/) {
                jreMinVersion = jreMinVersion + '.0'
            }
        }
        if (!jar) {
            if (project.plugins.hasPlugin('java')) {
                jar = "${libraryDir}/${project.tasks[JavaPlugin.JAR_TASK_NAME].archiveName}"
            } else {
                jar = ""
            }
        }
    }

    Object getCopyConfigurable() {
        return copyConfigurable
    }

    void setCopyConfigurable(Object copyConfigurable) {
        this.copyConfigurable = copyConfigurable
        onSetCopyConfigurable.execute(copyConfigurable)
    }

    public void onSetCopyConfigurable(Action<Object> action) {
        onSetCopyConfigurable.add(action)
    }

    public File getXmlOutFileForProject(Project project) {
        return project.file("${outputDir}/${xmlFileName}")
    }

    @Override
    int hashCode() {
        int result
        result = (launch4jCmd != null ? launch4jCmd.hashCode() : 0)
        result = 31 * result + (externalLaunch4j ? 1 : 0)
        result = 31 * result + (outputDir != null ? outputDir.hashCode() : 0)
        result = 31 * result + (libraryDir != null ? libraryDir.hashCode() : 0)
        result = 31 * result + (xmlFileName != null ? xmlFileName.hashCode() : 0)
        result = 31 * result + (mainClassName != null ? mainClassName.hashCode() : 0)
        result = 31 * result + (dontWrapJar ? 1 : 0)
        result = 31 * result + (headerType != null ? headerType.hashCode() : 0)
        result = 31 * result + (jar != null ? jar.hashCode() : 0)
        result = 31 * result + (outfile != null ? outfile.hashCode() : 0)
        result = 31 * result + (errTitle != null ? errTitle.hashCode() : 0)
        result = 31 * result + (cmdLine != null ? cmdLine.hashCode() : 0)
        result = 31 * result + (chdir != null ? chdir.hashCode() : 0)
        result = 31 * result + (priority != null ? priority.hashCode() : 0)
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0)
        result = 31 * result + (supportUrl != null ? supportUrl.hashCode() : 0)
        result = 31 * result + (stayAlive ? 1 : 0)
        result = 31 * result + (restartOnCrash ? 1 : 0)
        result = 31 * result + (manifest != null ? manifest.hashCode() : 0)
        result = 31 * result + (icon != null ? icon.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (textVersion != null ? textVersion.hashCode() : 0)
        result = 31 * result + (copyright != null ? copyright.hashCode() : 0)
        result = 31 * result + (opt != null ? opt.hashCode() : 0)
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (productName != null ? productName.hashCode() : 0)
        result = 31 * result + (internalName != null ? internalName.hashCode() : 0)
        result = 31 * result + (bundledJrePath != null ? bundledJrePath.hashCode() : 0)
        result = 31 * result + (bundledJre64Bit ? 1 : 0)
        result = 31 * result + (bundledJreAsFallback ? 1 : 0)
        result = 31 * result + (jreMinVersion != null ? jreMinVersion.hashCode() : 0)
        result = 31 * result + (jreMaxVersion != null ? jreMaxVersion.hashCode() : 0)
        result = 31 * result + (jdkPreference != null ? jdkPreference.hashCode() : 0)
        result = 31 * result + (jreRuntimeBits != null ? jreRuntimeBits.hashCode() : 0)
        result = 31 * result + (mutexName != null ? mutexName.hashCode() : 0)
        result = 31 * result + (windowTitle != null ? windowTitle.hashCode() : 0)
        result = 31 * result + (messagesStartupError != null ? messagesStartupError.hashCode() : 0)
        result = 31 * result + (messagesBundledJreError != null ? messagesBundledJreError.hashCode() : 0)
        result = 31 * result + (messagesJreVersionError != null ? messagesJreVersionError.hashCode() : 0)
        result = 31 * result + (messagesLauncherError != null ? messagesLauncherError.hashCode() : 0)
        result = 31 * result + (initialHeapSize != null ? initialHeapSize.hashCode() : 0)
        result = 31 * result + (initialHeapPercent != null ? initialHeapPercent.hashCode() : 0)
        result = 31 * result + (maxHeapSize != null ? maxHeapSize.hashCode() : 0)
        result = 31 * result + (maxHeapPercent != null ? maxHeapPercent.hashCode() : 0)
        result = 31 * result + (splashFileName != null ? splashFileName.hashCode() : 0)
        result = 31 * result + (splashWaitForWindows ? 1 : 0)
        result = 31 * result + (splashTimeout != null ? splashTimeout.hashCode() : 0)
        result = 31 * result + (splashTimeoutError ? 1 : 0)
        result = 31 * result + (copyConfigurable != null ? copyConfigurable.hashCode() : 0)
        return result
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Launch4jPluginExtension)) return false

        Launch4jPluginExtension that = (Launch4jPluginExtension) o

        if (bundledJre64Bit != that.bundledJre64Bit) return false
        if (bundledJreAsFallback != that.bundledJreAsFallback) return false
        if (dontWrapJar != that.dontWrapJar) return false
        if (externalLaunch4j != that.externalLaunch4j) return false
        if (restartOnCrash != that.restartOnCrash) return false
        if (splashTimeoutError != that.splashTimeoutError) return false
        if (splashWaitForWindows != that.splashWaitForWindows) return false
        if (stayAlive != that.stayAlive) return false
        if (bundledJrePath != that.bundledJrePath) return false
        if (chdir != that.chdir) return false
        if (cmdLine != that.cmdLine) return false
        if (companyName != that.companyName) return false
        if (copyright != that.copyright) return false
        if (description != that.description) return false
        if (downloadUrl != that.downloadUrl) return false
        if (errTitle != that.errTitle) return false
        if (headerType != that.headerType) return false
        if (icon != that.icon) return false
        if (initialHeapPercent != that.initialHeapPercent) return false
        if (initialHeapSize != that.initialHeapSize) return false
        if (internalName != that.internalName) return false
        if (jar != that.jar) return false
        if (jdkPreference != that.jdkPreference) return false
        if (jreMaxVersion != that.jreMaxVersion) return false
        if (jreMinVersion != that.jreMinVersion) return false
        if (jreRuntimeBits != that.jreRuntimeBits) return false
        if (launch4jCmd != that.launch4jCmd) return false
        if (mainClassName != that.mainClassName) return false
        if (manifest != that.manifest) return false
        if (maxHeapPercent != that.maxHeapPercent) return false
        if (maxHeapSize != that.maxHeapSize) return false
        if (messagesBundledJreError != that.messagesBundledJreError) return false
        if (messagesJreVersionError != that.messagesJreVersionError) return false
        if (messagesLauncherError != that.messagesLauncherError) return false
        if (messagesStartupError != that.messagesStartupError) return false
        if (mutexName != that.mutexName) return false
        if (opt != that.opt) return false
        if (outfile != that.outfile) return false
        if (outputDir != that.outputDir) return false
        if (libraryDir != that.libraryDir) return false
        if (priority != that.priority) return false
        if (productName != that.productName) return false
        if (splashFileName != that.splashFileName) return false
        if (splashTimeout != that.splashTimeout) return false
        if (supportUrl != that.supportUrl) return false
        if (textVersion != that.textVersion) return false
        if (version != that.version) return false
        if (windowTitle != that.windowTitle) return false
        if (xmlFileName != that.xmlFileName) return false
        if (copyConfigurable != that.copyConfigurable) return false

        return true
    }
}
