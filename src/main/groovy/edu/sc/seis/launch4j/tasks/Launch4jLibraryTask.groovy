/*
 * Copyright (c) 2016 Sebastian Boegl
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

import edu.sc.seis.launch4j.ExtractLibraries
import edu.sc.seis.launch4j.Launch4jPlugin
import org.gradle.api.GradleException
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class Launch4jLibraryTask extends DefaultLaunch4jTask {

    private static final String TEMPORARY_DIRECTORY = "tmp/launch4j"

    @TaskAction
    def run() {
        copyLibraries()
        def tmpDir = new File(project.buildDir, TEMPORARY_DIRECTORY)
        new ExtractLibraries(project).execute(tmpDir)
        createXML()
        createExecutableFolder()
        def stdOut = new ByteArrayOutputStream()
        def execResult = project.exec {
            commandLine "java", "-jar", "${tmpDir}/${ExtractLibraries.LAUNCH4J_BINARY_DIRECTORY}/launch4j-${Launch4jPlugin.ARTIFACT_VERSION}.jar", "${getXmlFile()}"
            workingDir getOutputDirectory()
            standardOutput = stdOut
            errorOutput = stdOut
            ignoreExitValue = true
        }

        if (execResult.exitValue == 0) {
            project.delete(getXmlFile())
        } else {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}")
        }
    }

}
