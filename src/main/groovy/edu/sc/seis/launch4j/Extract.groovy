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

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath

class Extract {

    static binaries(Project project) {
        binaries(project, project.configurations.getByName(Launch4jPlugin.LAUNCH4J_CONFIGURATION_NAME_BINARY))
    }

    static binaries(Project project, Configuration configuration) {
        def workingDirName = Launch4jPlugin.workdir()
        def destination = new File(project.buildDir, Launch4jPlugin.LAUNCH4J_BINARY_DIRECTORY)
        def jarName = "launch4j-${Launch4jPlugin.ARTIFACT_VERSION}-${workingDirName}"
        def workingJar = configuration.find { File file -> file.name =~ /${jarName}.jar/ }
        if (!workingJar) {
            throw new Exception("workingdir jar file not found!")
        }
        def lockFile = new File(destination, "created")
        if (!lockFile.exists()) {
            def copyOptions = {
                from { project.zipTree(workingJar) }
                includeEmptyDirs = false
                into { destination }
                eachFile { FileCopyDetails fcp ->
                    // only extract the binaries
                    if (fcp.relativePath.pathString.startsWith(jarName)) {
                        // remap the file to the root
                        def segments = fcp.relativePath.segments
                        def pathSegments = segments[1..-1] as String[]
                        fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathSegments)
                        fcp.mode = 0755
                    } else {
                        fcp.exclude()
                    }
                }
            }
            if (project.copy(copyOptions).didWork) {
                lockFile.text = new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"))
            }
        }
    }
}
