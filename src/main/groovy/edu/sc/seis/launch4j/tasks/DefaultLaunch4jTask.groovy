package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CopyLibraries
import edu.sc.seis.launch4j.Launch4jConfiguration
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.*

abstract class DefaultLaunch4jTask extends DefaultTask implements Launch4jConfiguration {

    Launch4jPluginExtension config

    protected DefaultLaunch4jTask() {
        this.config = project.launch4j
        project.afterEvaluate {
            if (project.hasProperty('shadowJar')) {
                dependsOn.add(project.tasks.getByName('shadowJar'))
            }
            if (project.hasProperty('fatJar')) {
                dependsOn.add(project.tasks.getByName('fatJar'))
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

    @Input
    @Optional
    String outfile

    @Override
    String getOutfile() {
        outfile ?: config.outfile
    }

    @Override
    @OutputFile
    File getDest() {
        project.file("${getOutputDirectory()}/${outfile ?: config.outfile}")
    }

    @Input
    @Optional
    String xmlFileName = "${name}.xml"

    @Override
    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName ?: config.xmlFileName}")
    }

    @Input
    @Optional
    String libraryDir

    @Override
    String getLibraryDir() {
        libraryDir ?: config.libraryDir
    }

    @InputFiles
    File getLibraryDirectory() {
        project.file("${getOutputDirectory()}/${libraryDir ?: config.libraryDir}")
    }

    @Input
    @Optional
    Object copyConfigurable

    def copyLibraries() {
        new CopyLibraries(project).execute(getLibraryDirectory(), copyConfigurable ?: config.copyConfigurable)
    }

    @Input
    @Optional
    String mainClassName

    @Override
    String getMainClassName() {
        mainClassName ?: config.mainClassName
    }
    @Input
    @Optional
    String jar

    @Override
    String getJar() {
        jar ? internalJar() : config.internalJar()
    }

    @Input
    @Optional
    Boolean dontWrapJar = false

    @Override
    Boolean getDontWrapJar() {
        dontWrapJar ?: config.dontWrapJar
    }

    @Input
    @Optional
    String headerType

    @Override
    String getHeaderType() {
        headerType ?: config.headerType
    }

    @Input
    @Optional
    String errTitle

    @Override
    String getErrTitle() {
        errTitle ?: config.errTitle
    }

    @Input
    @Optional
    String cmdLine

    @Override
    String getCmdLine() {
        cmdLine ?: config.cmdLine
    }

    @Input
    @Optional
    String chdir

    @Override
    String getChdir() {
        chdir ?: config.chdir
    }

    @Input
    @Optional
    String priority

    @Override
    String getPriority() {
        priority ?: config.priority
    }

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

    @Input
    @Optional
    Boolean stayAlive

    @Override
    Boolean getStayAlive() {
        stayAlive ?: config.stayAlive
    }

    @Input
    @Optional
    Boolean restartOnCrash

    @Override
    Boolean getRestartOnCrash() {
        restartOnCrash ?: config.stayAlive
    }

    @Input
    @Optional
    String manifest

    @Override
    String getManifest() {
        manifest ?: config.manifest
    }

    @Input
    @Optional
    String icon

    @Override
    String getIcon() {
        icon ?: config.icon
    }

    @Input
    @Optional
    String version

    @Override
    String getVersion() {
        version ?: config.version
    }

    @Input
    @Optional
    String textVersion

    @Override
    String getTextVersion() {
        textVersion ?: config.textVersion
    }

    @Input
    @Optional
    String copyright

    @Override
    String getCopyright() {
        copyright ?: config.copyright
    }

    @Input
    @Optional
    String opt

    @Override
    String getOpt() {
        opt ?: config.opt
    }

    @Input
    @Optional
    String companyName

    @Override
    String getCompanyName() {
        companyName ?: config.companyName
    }

    @Input
    @Optional
    String fileDescription

    @Override
    String getFileDescription() {
        fileDescription ?: config.fileDescription
    }

    @Input
    @Optional
    String productName

    @Override
    String getProductName() {
        productName ?: config.productName
    }

    @Input
    @Optional
    String internalName

    @Override
    String getInternalName() {
        internalName ?: config.internalName
    }

    @Input
    @Optional
    String trademarks

    @Override
    String getTrademarks() {
        trademarks ?: config.trademarks
    }

    @Input
    @Optional
    String language

    @Override
    String getLanguage() {
        language ?: config.language
    }
    @Input
    @Optional
    String bundledJrePath

    @Override
    String getBundledJrePath() {
        bundledJrePath ?: config.bundledJrePath
    }
    @Input
    @Optional
    Boolean bundledJre64Bit

    @Override
    Boolean getBundledJre64Bit() {
        bundledJre64Bit ?: config.bundledJre64Bit
    }
    @Input
    @Optional
    Boolean bundledJreAsFallback

    @Override
    Boolean getBundledJreAsFallback() {
        bundledJreAsFallback ?: config.bundledJreAsFallback
    }
    @Input
    @Optional
    String jreMinVersion

    @Override
    String getJreMinVersion() { jreMinVersion ? internalJreMinVersion() : config.internalJreMinVersion() }

    @Override
    String internalJreMinVersion() {
        if (!jreMinVersion) {
            if (project.hasProperty('targetCompatibility')) {
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
    @Input
    @Optional
    String jreMaxVersion

    @Override
    String getJreMaxVersion() {
        jreMaxVersion ?: config.jreMaxVersion
    }
    @Input
    @Optional
    String jdkPreference

    @Override
    String getJdkPreference() {
        jdkPreference ?: config.jdkPreference
    }
    @Input
    @Optional
    String jreRuntimeBits

    @Override
    String getJreRuntimeBits() {
        jreRuntimeBits ?: config.jreRuntimeBits
    }
    @Input
    @Optional
    String mutexName

    @Override
    String getMutexName() {
        mutexName ?: config.mutexName
    }
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
    Integer initialHeapSize

    @Override
    Integer getInitialHeapSize() {
        initialHeapSize ?: config.initialHeapSize
    }
    @Input
    @Optional
    Integer initialHeapPercent

    @Override
    Integer getInitialHeapPercent() {
        initialHeapPercent ?: config.initialHeapPercent
    }
    @Input
    @Optional
    Integer maxHeapSize

    @Override
    Integer getMaxHeapSize() {
        maxHeapSize ?: config.maxHeapSize
    }
    @Input
    @Optional
    Integer maxHeapPercent

    @Override
    Integer getMaxHeapPercent() {
        maxHeapPercent ?: config.maxHeapPercent
    }
    @Input
    @Optional
    String splashFileName

    @Override
    String getSplashFileName() {
        splashFileName ?: config.splashFileName
    }
    @Input
    @Optional
    Boolean splashWaitForWindows

    @Override
    Boolean getSplashWaitForWindows() {
        splashWaitForWindows ?: config.splashWaitForWindows
    }
    @Input
    @Optional
    Integer splashTimeout

    @Override
    Integer getSplashTimeout() {
        splashTimeout ?: config.splashTimeout
    }

    @Input
    @Optional
    Boolean splashTimeoutError

    @Override
    Boolean getSplashTimeoutError() {
        splashTimeoutError ?: config.splashTimeoutError
    }

    String internalJar() {
        if (!jar) {
            if (project.plugins.hasPlugin('java')) {
                jar = "${libraryDir}/${project.tasks[JavaPlugin.JAR_TASK_NAME].archiveName}"
            } else {
                jar = ""
            }
        }
        jar
    }

}
