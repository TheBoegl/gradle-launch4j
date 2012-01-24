package edu.sc.seis.launch4j

import org.gradle.api.internal.ConventionTask

import groovy.xml.MarkupBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CreateLaunch4jXMLTask extends DefaultTask {

    static final Logger LOGGER = LoggerFactory.getLogger(CreateLaunch4jXMLTask)


    @OutputFile
    File getXmlOutFile() {
        return project.file("$project.buildDir/launch4j/launch4j.xml")
    }

    @TaskAction
    def void writeXmlConfig() {
        Launch4jPluginExtension configuration = project.launch4j
        def classpath = project.configurations.runtime.collect { "lib/${it.name}" }
        def file = getXmlOutFile()
        file.parentFile.mkdirs()
        def writer = new BufferedWriter(new FileWriter(file))
        def xml = new MarkupBuilder(writer)
        xml.root {
            launch4jConfig() {
            dontWrapJar() {mkp.yield configuration.dontWrapJar}
            headerType() {mkp.yield configuration.headerType}
            jar() {mkp.yield configuration.jar}
            outfile() {mkp.yield configuration.outfile}
            errTitle() {mkp.yield configuration.errTitle}
            cmdLine() {mkp.yield configuration.cmdLine}
            chdir() {mkp.yield configuration.chdir}
            priority() {mkp.yield configuration.priority}
            downloadUrl() {mkp.yield configuration.downloadUrl}
            supportUrl() {mkp.yield configuration.supportUrl}
            customProcName() {mkp.yield configuration.customProcName}
            stayAlive() {mkp.yield configuration.stayAlive}
            manifest() {mkp.yield configuration.manifest}
            icon() {mkp.yield configuration.icon}
            classPath() {
                mainClass() {mkp.yield configuration.mainClassName}
                classpath.each() { val -> cp() { mkp.yield val } }
            }
            versionInfo() {
                fileVersion() { mkp.yield configuration.version }
                txtFileVersion() { mkp.yield configuration.version }
                fileDescription() { }
                copyright() { }
                productVersion() { mkp.yield configuration.version }
                txtProductVersion() { mkp.yield configuration.version }
                productName() { mkp.yield project.name }
                internalName() { mkp.yield project.name }
                originalFilename() { mkp.yield configuration.outfile }
            }
            }
        }
        writer.close()
    }

}
