package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CopyLibraries
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

abstract class DefaultLaunch4jTask extends DefaultTask {

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

    // ToDo: remove from here
    String outputDir
    @OutputDirectory
    File getOutputDirectory() {
        project.file("${project.buildDir}/${outputDir?:config.outputDir}")
    }

    String outfile
    @OutputFile
    File getDest() {
        project.file("${getOutputDirectory()}/${outfile?:config.outfile}")
    }

    String xmlFileName
//    @InputFile
    File getXmlFile() {
        project.file("${getOutputDirectory()}/${xmlFileName?:config.xmlFileName}")
    }

    String libraryDir
    //    task.inputs.dir("${-> configuration.outputDir}/${-> configuration.libraryDir}")
    @InputFiles File getLibraryDirectory() {
        project.file("${getOutputDirectory()}/${libraryDir?:config.libraryDir}")
    }

    Object copyConfigurable

    def copyLibraries() {
        new CopyLibraries(project).execute(getLibraryDirectory(), copyConfigurable?:config.copyConfigurable)
    }

}
