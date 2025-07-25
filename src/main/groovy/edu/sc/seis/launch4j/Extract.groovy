/*
 * Copyright (c) 2025 Sebastian Boegl
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

import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.FileTree
import org.gradle.api.file.RelativePath
import org.gradle.api.internal.file.FileOperations

import java.nio.file.Files

class Extract {

    static void binaries(FileTree launch4jZipTree, FileOperations fileOperations, FileCollection configuration, File destinationParentFolder) {
        def destination = destinationParentFolder.toPath()
        Files.createDirectories(destination)
        def lockFile = destination.resolve("created").toFile()
        if (!lockFile.exists()) {
            def copy = fileOperations.copy {
                it.from {  launch4jZipTree }
                it.includeEmptyDirs = false
                it.into { destination }
                it.eachFile { FileCopyDetails fcp ->
                    // only extract the binaries
                    if (fcp.relativePath.pathString.startsWith("launch4j-")) {
                        // remap the file to the root
                        def segments = fcp.relativePath.segments
                        def pathSegments = segments[1..-1] as String[]
                        fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathSegments)
                        // we are unable to set the file permissions but the deprecated methods FileCopyDetails#setMode(int) or CopySpec#setMode(int) would at least not throw an exception
                    } else {
                        fcp.exclude()
                    }
                }
            }
            if (copy.didWork) {
                lockFile.text = new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC"))
            } else {
                throw new Exception("the unpacking of launch4j ${destination.getFileName().toString()} failed")
            }
        }
    }
}
