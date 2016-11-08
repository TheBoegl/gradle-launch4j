package edu.sc.seis.launch4j

import groovy.transform.AutoClone
import groovy.transform.PackageScope
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input

@AutoClone(excludes = [''])
class Launch4jPluginExtension {

    private final Project project

    Launch4jPluginExtension(Project project) {
        this.project = project
    }

    String mainClassName
    String jar

    @Input String outputDir = "launch4j"
    File getOutputDirectory() {
        project.file("${project.buildDir}/${outputDir}")
    }

    String libraryDir = "lib"
    String xmlFileName = "launch4j.xml"
    boolean dontWrapJar = false
    String headerType = "gui"
    String outfile = "${project.name}.exe"
    File getDest() {
        project.file("${getOutputDirectory()}/${outfile}")
    }
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
    String version = "${project.version}"
    String textVersion = "${project.version}"
    String copyright = "unknown"
    String opt = ""
    String companyName = ""
    String description = "${project.name}"
    String productName = "${project.name}"
    String internalName = "${project.name}"
    String trademarks = ""
    String language = "ENGLISH_US"

    String bundledJrePath
    boolean bundledJre64Bit = false
    boolean bundledJreAsFallback = false
    String jreMinVersion
    @PackageScope String internalJreMinVersion() {
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
        jreMinVersion
    }
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

    transient Object copyConfigurable

    File getXmlOutFile() {
        project.file("${getOutputDirectory()}/${xmlFileName}")
    }

    String internalJar() {
        if(!jar) {
            if (project.plugins.hasPlugin('java')) {
                jar = "${libraryDir}/${project.tasks[JavaPlugin.JAR_TASK_NAME].archiveName}"
            } else {
                jar = ""
            }
        }
        jar
    }
}
