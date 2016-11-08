package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CreateXML
import edu.sc.seis.launch4j.ExtractLibraries
import edu.sc.seis.launch4j.Launch4jPlugin
import edu.sc.seis.launch4j.Launch4jPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class Launch4jLibraryTask extends DefaultLaunch4jTask {

    @TaskAction
    def run() {
        Launch4jPluginExtension config = project.launch4j.clone()
        if (outfile) {
            config.outfile = outfile
        }
        if (outputDir) {
            config.outputDir = outputDir
        }
        if (xmlFileName) {
            config.xmlFileName = xmlFileName
        }
        if (libraryDir) {
            config.libraryDir = libraryDir
        }
        copyLibraries()
        new ExtractLibraries(project).execute(getOutputDirectory())
        new CreateXML(project).execute(getXmlFile(), config);
        getDest().delete()
        def stdOut = new ByteArrayOutputStream()
        def execResult = project.exec {
            commandLine "java", "-jar", "${ExtractLibraries.LAUNCH4J_BINARY_DIRECTORY}/launch4j-${Launch4jPlugin.ARTIFACT_VERSION}.jar", "${getXmlFile()}"
            workingDir getOutputDirectory()
            standardOutput = stdOut
            errorOutput = stdOut
            ignoreExitValue = true
        }

        if (execResult.exitValue != 0) {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}");
        }
    }

}
