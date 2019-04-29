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

import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.bundling.Jar

@CompileStatic
@AutoClone
class Launch4jPluginExtension implements Launch4jConfiguration {

    private final Project project
    final FileOperations fileOperations

    Launch4jPluginExtension(Project project, FileOperations fileOperations) {
        this.project = project
        this.fileOperations = fileOperations
    }

    String mainClassName
    String jar

    @Input
    String outputDir = 'launch4j'

    File getOutputDirectory() {
        project.file("${project.buildDir}/${outputDir}")
    }

    String libraryDir = 'lib'
    String xmlFileName = 'launch4j.xml'
    Boolean dontWrapJar = false
    String headerType = 'gui'

    String outfile = "${project.name}.exe"

    File getDest() {
        project.file("${getOutputDirectory()}/${outfile}")
    }
    String errTitle = ''
    String cmdLine = ''
    String chdir = '.'
    String priority = 'normal'
    String downloadUrl = 'http://java.com/download'
    String supportUrl = ''
    Boolean stayAlive = false
    Boolean restartOnCrash = false
    String manifest = ''
    String icon = ''
    String version = "${project.version}"
    String textVersion = "${project.version}"
    String copyright = 'unknown'

    Set<String> jvmOptions = []

    @Deprecated
    void setOpt(String opt) {
        if (!opt) return // null check
        this.jvmOptions = [ opt ] as Set
        project.logger.warn("${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.opt property is deprecated. Use ${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.jvmOptions instead.")
    }

    String companyName = ''
    String fileDescription = "${project.name}"

    @Deprecated
    void setDescription(String description) {
        fileDescription = description
        project.logger.warn("${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.description property is deprecated. Use ${Launch4jPlugin.LAUNCH4J_EXTENSION_NAME}.fileDescription instead.")
    }

    @Deprecated
    String getDescription() {
        return fileDescription
    }
    String productName = "${project.name}"
    String internalName = "${project.name}"
    String trademarks = ''
    String language = 'ENGLISH_US'

    String bundledJrePath
    Boolean bundledJre64Bit = false
    Boolean bundledJreAsFallback = false
    String jreMinVersion

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
    String jreMaxVersion
    String jdkPreference = 'preferJre'
    String jreRuntimeBits = '64/32'

    Set<String> variables = []

    String mutexName
    String windowTitle

    String messagesStartupError
    String messagesBundledJreError
    String messagesJreVersionError
    String messagesLauncherError
    String messagesInstanceAlreadyExists

    Integer initialHeapSize
    Integer initialHeapPercent
    Integer maxHeapSize
    Integer maxHeapPercent

    String splashFileName
    Boolean splashWaitForWindows = true
    Integer splashTimeout = 60
    Boolean splashTimeoutError = true

    transient Object copyConfigurable

    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName}")
    }

    String internalJar() {
        if (!jar) {
            if (project.plugins.hasPlugin('java')) {
                def jarTask = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
                jar = "${libraryDir}/${jarTask.archiveName}"
            } else {
                jar = ""
            }
        }
        jar
    }

    Set<String> classpath = []
}
