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

import groovy.xml.MarkupBuilder
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

import java.nio.file.Path
import java.nio.file.Paths

class CreateXML {

    private final Project project

    CreateXML(Project project) {
        this.project = project
    }

    void execute(Launch4jPluginExtension l4j) {
        execute(l4j.getXmlFile(), l4j, null)
    }

    void execute(File xmlFile, Launch4jConfiguration config, FileCollection copySpec) {
        def outputDir = config.getOutputDirectory()
        outputDir.mkdirs()
        def outFilePath = config.getDest().parentFile.toPath()
        def classpath
        if (config.classpath) {
            classpath = config.classpath
        } else if (copySpec instanceof FileCollection) {
            classpath = copySpec.collect {
                outFilePath.relativize(it.toPath()).toString()
            }
        } else {
            classpath = (copySpec ?: (project.plugins.hasPlugin('java') ? project.configurations.runtime : [])).collect {
                outFilePath.relativize(outputDir.toPath().resolve(Paths.get(config.libraryDir, it.name))).toString()
                // relativize paths relative to outfile
            }
        }
        def jar = config.dontWrapJar ? outFilePath.relativize(outputDir.toPath().resolve(Paths.get(config.jar))) : config.jar
        def writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8"));
        def xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
        xml.launch4jConfig() {
            xml.dontWrapJar(config.dontWrapJar)
            xml.headerType(config.headerType)
            xml.jar(jar)
            xml.outfile(config.outfile)
            xml.errTitle(config.errTitle)
            xml.cmdLine(config.cmdLine)
            xml.chdir(config.chdir)
            xml.priority(config.priority)
            xml.downloadUrl(config.downloadUrl)
            xml.supportUrl(config.supportUrl)
            xml.stayAlive(config.stayAlive)
            xml.restartOnCrash(config.restartOnCrash)
            xml.manifest(config.manifest)
            xml.icon(relativizeIfAbsolute(outFilePath, config.icon))
            config.variables.each { var -> xml.var(var) }
            if (config.mainClassName) {
                xml.classPath() {
                    mainClass(config.mainClassName)
                    classpath.each() { val -> xml.cp(val) }
                }
            }
            jre() {
                xml.path(config.bundledJrePath != null ? config.bundledJrePath : "")
                xml.bundledJre64Bit(config.bundledJre64Bit)
                xml.bundledJreAsFallback(config.bundledJreAsFallback)
                def minVersion = config.jreMinVersion == null && config.bundledJrePath != null ? "" : config.jreMinVersion ? config.jreMinVersion : config.internalJreMinVersion()
                xml.minVersion(minVersion)
                xml.maxVersion(config.jreMaxVersion != null ? config.jreMaxVersion : "")
                xml.jdkPreference(config.jdkPreference)
                xml.runtimeBits(config.jreRuntimeBits)

                config.jvmOptions.each { opt ->
                    if (opt) {
                        xml.opt(opt)
                    }
                }

                if (config.initialHeapSize != null)
                    xml.initialHeapSize(config.initialHeapSize)

                if (config.initialHeapPercent != null)
                    xml.initialHeapPercent(config.initialHeapPercent)

                if (config.maxHeapSize != null)
                    xml.maxHeapSize(config.maxHeapSize)

                if (config.maxHeapPercent != null)
                    xml.maxHeapPercent(config.maxHeapPercent)
            }
            if (config.splashFileName != null && config.splashTimeout != null) {
                splash() {
                    xml.file(relativizeIfAbsolute(outFilePath, config.splashFileName))
                    xml.waitForWindow(config.splashWaitForWindows)
                    xml.timeout(config.splashTimeout)
                    xml.timeoutErr(config.splashTimeoutError)
                }
            }
            versionInfo() {
                xml.fileVersion(parseDotVersion(config.version))
                xml.txtFileVersion(config.textVersion)
                xml.fileDescription(config.fileDescription)
                xml.copyright(config.copyright)
                xml.productVersion(parseDotVersion(config.version))
                xml.txtProductVersion(config.textVersion)
                xml.productName(config.productName)
                xml.companyName(config.companyName)
                xml.internalName(config.internalName)
                xml.originalFilename(config.outfile)
                xml.trademarks(config.trademarks)
                xml.language(config.language)
            }

            if (config.messagesStartupError != null ||
                config.messagesBundledJreError != null ||
                config.messagesJreVersionError != null ||
                config.messagesLauncherError != null
                || config.messagesInstanceAlreadyExists != null
            ) {
                messages() {
                    if (config.messagesStartupError != null)
                        xml.startupErr(config.messagesStartupError)
                    if (config.messagesBundledJreError != null)
                        xml.bundledJreErr(config.messagesBundledJreError)
                    if (config.messagesJreVersionError != null)
                        xml.jreVersionErr(config.messagesJreVersionError)
                    if (config.messagesLauncherError != null)
                        xml.launcherErr(config.messagesLauncherError)
                    if (config.messagesInstanceAlreadyExists != null)
                        xml.instanceAlreadyExistsMsg(config.messagesInstanceAlreadyExists)
                }
            }
            if (config.mutexName != null || config.windowTitle != null) {
                singleInstance() {
                    if (config.mutexName != null)
                        xml.mutexName(config.mutexName)

                    if (config.windowTitle != null)
                        xml.windowTitle(config.windowTitle)
                }
            }
        }
        writer.close()
    }

    /**
     * Relativizes the other path from the source path if, and only if, it is absolute.
     *
     * @param source The source path to relativize from.
     * @param other the path to relativize against the source path.
     * @return the resulting relative path, or an empty path if both paths are equal
     * @see Path#relativize(Path)
     */
    static String relativizeIfAbsolute(Path source, String other) {
        def path = Paths.get(other)
        if (path.absolute) {
            path = source.relativize(path)
        }
        return path
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
