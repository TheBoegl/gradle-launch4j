package edu.sc.seis.launch4j

import groovy.xml.MarkupBuilder
import org.gradle.api.Project

import java.nio.file.Paths

class CreateXML {

    private final Project project

    CreateXML(Project project) {
        this.project = project
    }

    void execute(Launch4jPluginExtension l4j) {
        execute(l4j.getXmlOutFile(), l4j)
    }

    void execute(File xmlFile, Launch4jPluginExtension l4j) {
        def outputDir = l4j.getOutputDirectory()
        outputDir.mkdirs()
        def outFilePath = l4j.getDest().parentFile.toPath()
        def classpath = project.plugins.hasPlugin('java') ? project.configurations.runtime.collect {
            outFilePath.relativize(outputDir.toPath().resolve(Paths.get(l4j.libraryDir, it.name))).toString() // relativize paths relative to outfile
        } : []
        def writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8"));
        def xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
        xml.launch4jConfig() {
            xml.dontWrapJar(l4j.dontWrapJar)
            xml.headerType(l4j.headerType)
            xml.jar(l4j.internalJar())
            xml.outfile(l4j.outfile)
            xml.errTitle(l4j.errTitle)
            xml.cmdLine(l4j.cmdLine)
            xml.chdir(l4j.chdir)
            xml.priority(l4j.priority)
            xml.downloadUrl(l4j.downloadUrl)
            xml.supportUrl(l4j.supportUrl)
            xml.stayAlive(l4j.stayAlive)
            xml.restartOnCrash(l4j.restartOnCrash)
            xml.manifest(l4j.manifest)
            xml.icon(l4j.icon)
            if (l4j.mainClassName) {
                xml.classPath() {
                    mainClass(l4j.mainClassName)
                    classpath.each() { val -> xml.cp(val) }
                }
            }
            jre() {
                xml.path(l4j.bundledJrePath != null ? l4j.bundledJrePath : "")
                xml.bundledJre64Bit(l4j.bundledJre64Bit)
                xml.bundledJreAsFallback(l4j.bundledJreAsFallback)
                xml.minVersion(l4j.internalJreMinVersion())
                xml.maxVersion(l4j.jreMaxVersion != null ? l4j.jreMaxVersion : "")
                xml.jdkPreference(l4j.jdkPreference)
                xml.runtimeBits(l4j.jreRuntimeBits)

                if (l4j.opt.length() != 0) xml.opt(l4j.opt)

                if (l4j.initialHeapSize != null)
                    xml.initialHeapSize(l4j.initialHeapSize)

                if (l4j.initialHeapPercent != null)
                    xml.initialHeapPercent(l4j.initialHeapPercent)

                if (l4j.maxHeapSize != null)
                    xml.maxHeapSize(l4j.maxHeapSize)

                if (l4j.maxHeapPercent != null)
                    xml.maxHeapPercent(l4j.maxHeapPercent)
            }
            if (l4j.splashFileName != null && l4j.splashTimeout != null) {
                splash() {
                    xml.file(l4j.splashFileName)
                    xml.waitForWindow(l4j.splashWaitForWindows)
                    xml.timeout(l4j.splashTimeout)
                    xml.timeoutErr(l4j.splashTimeoutError)
                }
            }
            versionInfo() {
                xml.fileVersion(parseDotVersion(l4j.version))
                xml.txtFileVersion(l4j.textVersion)
                xml.fileDescription(l4j.description)
                xml.copyright(l4j.copyright)
                xml.productVersion(parseDotVersion(l4j.version))
                xml.txtProductVersion(l4j.textVersion)
                xml.productName(l4j.productName)
                xml.companyName(l4j.companyName)
                xml.internalName(l4j.internalName)
                xml.originalFilename(l4j.outfile)
                xml.trademarks(l4j.trademarks)
                xml.language(l4j.language)
            }

            if (l4j.messagesStartupError != null ||
                    l4j.messagesBundledJreError != null ||
                    l4j.messagesJreVersionError != null ||
                    l4j.messagesLauncherError != null) {
                messages() {
                    if (l4j.messagesStartupError != null)
                        xml.startupErr(l4j.messagesStartupError)
                    if (l4j.messagesBundledJreError != null)
                        xml.bundledJreErr(l4j.messagesBundledJreError)
                    if (l4j.messagesJreVersionError != null)
                        xml.jreVersionErr(l4j.messagesJreVersionError)
                    if (l4j.messagesLauncherError != null)
                        xml.launcherErr(l4j.messagesLauncherError)
                }
            }
            if (l4j.mutexName != null || l4j.windowTitle != null) {
                singleInstance() {
                    if (l4j.mutexName != null)
                        xml.mutexName(l4j.mutexName)

                    if (l4j.windowTitle != null)
                        xml.windowTitle(l4j.windowTitle)
                }
            }
        }
        writer.close()
    }

    /**
     * launch4j fileVersion and productVersion are required to be x.y.z.w format, no text like beta or
     * SNAPSHOT. I think this is a windows thing. So we check the version, and if it is only dots and
     * numbers, we use it. If not we use 0.0.0.1
     * @param version
     * @return
     */
    static String parseDotVersion(version) {
        if (version ==~ /\d+(\.\d+){3}/) {
            return version
        } else if (version ==~ /\d+(\.\d+){0,2}/) {
            def s = version + '.0'
            while (s ==~ /\d+(\.\d+){0,2}/) {
                s = s + '.0'
            }
            return s
        } else {
            return '0.0.0.1'
        }
    }
}
