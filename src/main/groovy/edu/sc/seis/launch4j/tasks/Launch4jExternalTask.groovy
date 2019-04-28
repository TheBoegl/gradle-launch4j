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

package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.Extract
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class Launch4jExternalTask extends DefaultLaunch4jTask {

    @Input
    String launch4jCmd = "launch4j"

    @TaskAction
    def run() {
        Extract.binaries(project)
        createXML(copyLibraries())
        createExecutableFolder()
        def stdOut = new ByteArrayOutputStream()
        def execResult = project.exec {
            commandLine "${launch4jCmd}", "${getXmlFile()}"
            workingDir getOutputDirectory()
            standardOutput = stdOut
            errorOutput = stdOut
            ignoreExitValue = true
        }

        if (execResult.exitValue != 0) {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}")
        }
//        else {
//            //return value not set in launch4j 3.8.0, so test the outcome by iterating over the expected output files
//            if (!getDest().exists()) {
//                throw new GradleException("${outfile.name} not created:\n\t${stdOut.toString()}")
//            }
//        }
    }

}
