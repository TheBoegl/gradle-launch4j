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

import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.internal.file.FileOperations
import org.gradle.api.model.ObjectFactory

import java.nio.file.Path

class CopyLibraries {
    ObjectFactory objectFactory
    FileOperations fileOperations
    DuplicatesStrategy duplicatesStrategy

    CopyLibraries(ObjectFactory objectFactory, FileOperations fileOperations, DuplicatesStrategy duplicatesStrategy) {
        this.objectFactory = objectFactory
        this.fileOperations = fileOperations
        this.duplicatesStrategy = duplicatesStrategy
    }

    /**
     * Copies the project dependency jars to the configured library directory
     * @param libraryDir
     */
    FileCollection execute(File libraryDir, Object copyConfigurable, Path jarPath, FileCollection runtimeClasspath, ConfigurableFileCollection configurableFileCollection) {
        def files = []
        fileOperations.sync(new Action<CopySpec>() {
            void execute(CopySpec t) {
                t.duplicatesStrategy = duplicatesStrategy
                t.into(libraryDir)
                if (copyConfigurable != null) {
                    if (copyConfigurable instanceof CopySpec) {
                        t.with(copyConfigurable)
                    } else {
                        t.with {
                            from { copyConfigurable }
                        }
                    }
                } else {
                    if (jarPath) {
                        t.with {
                            from { jarPath }
                        }
                    }
                    if (runtimeClasspath) {
                        t.with {
                            from(runtimeClasspath)
                        }
                    }
                }
                t.eachFile { FileCopyDetails details ->
                    files.add(details.relativePath.getFile(libraryDir))
                }
            }
        })
        configurableFileCollection.from(files)
    }
}
