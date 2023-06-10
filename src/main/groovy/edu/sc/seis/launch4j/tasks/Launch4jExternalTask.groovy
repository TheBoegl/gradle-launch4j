/*
 * Copyright (c) 2023 Sebastian Boegl
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
import edu.sc.seis.launch4j.PropertyUtils
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.util.GradleVersion

import javax.inject.Inject

class Launch4jExternalTask extends DefaultLaunch4jTask {

    @Input
    final Property<String> launch4jCmd
    private final Object execOperations

    Launch4jExternalTask() {
        if (GradleVersion.current() >= GradleVersion.version('6.0')) {
            this.execOperations = project.objects.newInstance(InjectedExecOperations).execOperations
        } else {
            this.execOperations = null;
        }
        launch4jCmd = PropertyUtils.assign(project.objects.property(String), "launch4j")
    }



    @TaskAction
    def run() {
        Extract.binaries(launch4jZipTree.get(), config.fileOperations, launch4jBinaryFiles, launch4jBinaryDirectory.get().asFile)
        createXML(copyLibraries())
        createExecutableFolder()
        def stdOut = new ByteArrayOutputStream()
        def execResult
        if (execOperations != null) {
            execResult = ((ExecOperations)execOperations).exec {
                commandLine "${launch4jCmd}", "${xmlFile.get().asFile}"
                workingDir getOutputDirectory()
                standardOutput = stdOut
                errorOutput = stdOut
                ignoreExitValue = true
            }
        } else {
            execResult = project.exec {
                commandLine "${launch4jCmd}", "${xmlFile.get().asFile}"
                workingDir getOutputDirectory()
                standardOutput = stdOut
                errorOutput = stdOut
                ignoreExitValue = true
            }
        }

        if (execResult.exitValue != 0) {
            throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${stdOut.toString()}")
        }
    }

    static class InjectedExecOperations {
        ExecOperations execOperations

        @Inject
        InjectedExecOperations(ExecOperations execOperations) {
            this.execOperations = execOperations
        }
    }
}
