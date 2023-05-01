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
import org.gradle.api.provider.Property

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
        def outputDir = config.getOutputDirectory().get().asFile
        outputDir.mkdirs()
        def outFilePath = config.getDest().parentFile.toPath()
        def classpath
        if (config.classpath.isPresent()) {
            classpath = config.classpath.get()
        } else if (copySpec instanceof FileCollection) {
            classpath = copySpec.collect {
                outFilePath.relativize(it.toPath()).toString()
            }
        } else {
            classpath = (copySpec ?:
                (project.plugins.hasPlugin('java') ?
                    (project.configurations.findByName('runtimeClasspath') ?
                        project.configurations.runtimeClasspath : project.configurations.runtime) : [])).collect {
                outFilePath.relativize(outputDir.toPath().resolve(Paths.get(config.libraryDir, it.name))).toString()
                // relativize paths relative to outfile
            }
        }
        def jarTaskOutputPath = config.getJarTaskOutputPath()
        def jar
        if (config.dontWrapJar) {
            /**
             * Priority of sources for path to jar:
             * 1. `jarTask` (output file resolved against libraryDirectory), if not set then:
             * 2. `jar` (resolved against outputDirectory), if not set then:
             * 3. `jarTask` default fallback if Java plugin was applied (tasks[jar]) (output file resolved against libraryDirectory)
             * 4. null
             */
            def jarPath
            if (jarTaskOutputPath) {
                jarPath = config.getLibraryDirectory().toPath().resolve(jarTaskOutputPath.fileName)
            } else if (config.getJar()) {
                jarPath = outputDir.toPath().resolve(config.getJar())
            } else if (config.getJarTaskDefaultOutputPath()) {
                jarPath = config.getLibraryDirectory().toPath().resolve(config.getJarTaskDefaultOutputPath().fileName)
            } else {
                jarPath = null
            }

            jar = jarPath ? outFilePath.relativize(jarPath) : ""
        } else {
            /**
             * Priority of sources for path to jar:
             * 1. `jarTask`, if not set then:
             * 2. `jar`, if not set then:
             * 3. `jarTask` default fallback if Java plugin was applied (tasks[jar])
             * 4. null
             */
            jar = jarTaskOutputPath ?: (config.getJar() ? outputDir.toPath().resolve(config.getJar()) : config.getJarTaskDefaultOutputPath()) ?: ""
        }
        def writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFile), "UTF-8"))
        def xml = new MarkupBuilder(writer)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8")
        xml.launch4jConfig() {
            xml.dontWrapJar(config.dontWrapJar.get())
            xml.headerType(config.headerType.get())
            xml.jar(jar)
            xml.outfile(config.outfile.get())
            xml.errTitle(config.errTitle.get())
            xml.cmdLine(config.cmdLine.get())
            xml.chdir(config.chdir.get())
            xml.priority(config.priority.get())
            xml.downloadUrl(config.downloadUrl.get())
            xml.supportUrl(config.supportUrl.get())
            xml.stayAlive(config.stayAlive.get())
            xml.restartOnCrash(config.restartOnCrash.get())
            xml.manifest(config.manifest.get())
            xml.icon(relativizeIfAbsolute(outFilePath, config.icon.get()))
            if (config.variables.isPresent()) {
                config.variables.get().each { var -> xml.var(var) }
            }
            if (config.mainClassName.isPresent()) {
                xml.classPath() {
                    mainClass(config.mainClassName.get())
                    classpath.each() { val -> xml.cp(val) }
                }
            }
            jre() {
                xml.path(config.bundledJrePath.getOrElse(""))
                xml.bundledJre64Bit(config.bundledJre64Bit.get())
                xml.bundledJreAsFallback(config.bundledJreAsFallback.get())
                def minVersion = !config.jreMinVersion.isPresent() && config.bundledJrePath.isPresent() ? "" : config.jreMinVersion ? config.jreMinVersion : config.internalJreMinVersion()
                xml.minVersion(minVersion instanceof Property ? minVersion.get() : minVersion)
                xml.maxVersion(config.jreMaxVersion.getOrElse(""))
                xml.jdkPreference(config.jdkPreference.get())
                xml.runtimeBits(config.jreRuntimeBits.get())

                if (config.jvmOptions.isPresent()) {
                    config.jvmOptions.get().each { opt ->
                        xml.opt(opt)
                    }
                }

                if (config.initialHeapSize.isPresent())
                    xml.initialHeapSize(config.initialHeapSize.get())

                if (config.initialHeapPercent.isPresent())
                    xml.initialHeapPercent(config.initialHeapPercent.get())

                if (config.maxHeapSize.isPresent())
                    xml.maxHeapSize(config.maxHeapSize.get())

                if (config.maxHeapPercent.isPresent())
                    xml.maxHeapPercent(config.maxHeapPercent.get())
            }
            if (config.splashFileName.isPresent() && config.splashTimeout.isPresent()) {
                splash() {
                    xml.file(relativizeIfAbsolute(outFilePath, config.splashFileName.get()))
                    xml.waitForWindow(config.splashWaitForWindows.get())
                    xml.timeout(config.splashTimeout.get())
                    xml.timeoutErr(config.splashTimeoutError.get())
                }
            }
            versionInfo() {
                xml.fileVersion(parseDotVersion(config.version.get()))
                xml.txtFileVersion(config.textVersion.get())
                xml.fileDescription(config.fileDescription.get())
                xml.copyright(config.copyright.get())
                xml.productVersion(parseDotVersion(config.version.get()))
                xml.txtProductVersion(config.textVersion.get())
                xml.productName(config.productName.get())
                xml.companyName(config.companyName.get())
                xml.internalName(config.internalName.get())
                xml.originalFilename(config.outfile.get())
                xml.trademarks(config.trademarks.get())
                xml.language(config.language.get())
            }

            if (config.messagesStartupError.isPresent() ||
                config.messagesBundledJreError.isPresent() ||
                config.messagesJreVersionError.isPresent() ||
                config.messagesLauncherError.isPresent()
                || config.messagesInstanceAlreadyExists.isPresent()
            ) {
                messages() {
                    if (config.messagesStartupError.isPresent())
                        xml.startupErr(config.messagesStartupError.get())
                    if (config.messagesBundledJreError.isPresent())
                        xml.bundledJreErr(config.messagesBundledJreError.get())
                    if (config.messagesJreVersionError.isPresent())
                        xml.jreVersionErr(config.messagesJreVersionError.get())
                    if (config.messagesLauncherError.isPresent())
                        xml.launcherErr(config.messagesLauncherError.get())
                    if (config.messagesInstanceAlreadyExists.isPresent())
                        xml.instanceAlreadyExistsMsg(config.messagesInstanceAlreadyExists.get())
                }
            }
            if (config.mutexName.isPresent() || config.windowTitle.isPresent()) {
                singleInstance() {
                    if (config.mutexName.isPresent())
                        xml.mutexName(config.mutexName.get())

                    if (config.windowTitle.isPresent())
                        xml.windowTitle(config.windowTitle.get())
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
