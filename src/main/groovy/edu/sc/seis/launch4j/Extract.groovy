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
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath

import java.nio.file.Files

class Extract {

    static File binaries(Project project, FileCollection configuration, File destinationParentFolder) {
        def workingDirName = Launch4jPlugin.workdir()
        def jarName = "launch4j-(\\d{1,2}\\.\\d{1,2})-${workingDirName}"
        def workingJar = configuration.find { File file -> file.name =~ /${jarName}.jar/ }
        if (!workingJar) {
            throw new Exception("launch4j binary jar ${workingDirName} file not found! Expected ${workingDirName}.jar but got ${configuration.files.collect {it.name}}. Use the correct classifier for this platform.")
        }
        def m = workingJar.name =~ /launch4j-(\d{1,2}\.\d{1,2})/
        def version = m? m[0][1] : workingJar.name
        def destination = destinationParentFolder.toPath().resolve(version)
        Files.createDirectories(destination)
        def lockFile = destination.resolve("created").toFile()
        if (!lockFile.exists()) {
            def copyOptions = {
                from { project.zipTree(workingJar) }
                includeEmptyDirs = false
                into { destination }
                eachFile { FileCopyDetails fcp ->
                    // only extract the binaries
                    if (fcp.relativePath.pathString.startsWith("launch4j-")) {
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
            } else {
                throw new Exception("the unpacking of launch4j $version failed")
            }
        }
        destination.toFile()
    }
}
