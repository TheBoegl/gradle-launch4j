package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CopyLibraries
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

abstract class DefaultLaunch4jTask extends DefaultTask {

    Launch4jPluginExtension config

    protected DefaultLaunch4jTask() {
        this.config = project.launch4j
    }

    String outputDir
    @OutputDirectory
    File getOutputDirectory() {
        project.file("${project.buildDir}/${outputDir?:config.outputDir}")
    }

    String outfile
    File getDest() {
        project.file("${getOutputDirectory()}/${outfile?:config.outfile}")
    }

    String xmlFileName
    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName?:config.xmlFileName}")
    }

    String libraryDir
    @InputFiles File getLibraryDirectory() {
        project.file("${getOutputDirectory()}/${libraryDir?:config.libraryDir}")
    }

    Object copyConfigurable

    def copyLibraries() {
        new CopyLibraries(project).execute(getLibraryDirectory(), copyConfigurable?:config.copyConfigurable)
    }

}
