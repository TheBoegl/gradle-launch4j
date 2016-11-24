package edu.sc.seis.launch4j.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Launch4jExternalTask extends DefaultLaunch4jTask {

    @Input String launch4jCmd = "launch4j"

    @TaskAction
    def run() {
        copyLibraries()
        createXML()
        def stdOut = new ByteArrayOutputStream()
        def execResult = project.exec {
            commandLine "${launch4jCmd}", "${getXmlFile()}"
            workingDir getOutputDirectory()
            standardOutput = stdOut
            errorOutput = stdOut
            ignoreExitValue = true
        }

        if (execResult.exitValue != 0) {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}");
        }
//        else {
//            //return value not set in launch4j 3.8.0, so test the outcome by iterating over the expected output files
//            if (!getDest().exists()) {
//                throw new GradleException("${outfile.name} not created:\n\t${stdOut.toString()}")
//            }
//        }
    }

}
