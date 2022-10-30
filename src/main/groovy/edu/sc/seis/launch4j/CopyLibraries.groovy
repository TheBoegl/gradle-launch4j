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
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.internal.file.FileOperations
import java.nio.file.Path

class CopyLibraries {
    Project project
    FileOperations fileOperations
    DuplicatesStrategy duplicatesStrategy

    CopyLibraries(Project project, FileOperations fileOperations, DuplicatesStrategy duplicatesStrategy) {
        this.project = project
        this.fileOperations = fileOperations
        this.duplicatesStrategy = duplicatesStrategy
    }

    /**
     * Copies the project dependency jars to the configured library directory
     * @param libraryDir
     */
    FileCollection execute(File libraryDir, Object copyConfigurable, Path jarPath) {
        def files = []
        def distSpec = {
            if (copyConfigurable != null) {
                if (copyConfigurable instanceof CopySpec) {
                    with(copyConfigurable)
                } else {
                    with {
                        from { copyConfigurable }
                    }
                }
            } else {
                if (jarPath) {
                    with {
                        from { jarPath }
                    }
                }
                if (project.plugins.hasPlugin('java')) {
                    with {
                        from(project.configurations.findByName('runtimeClasspath') ?
                            project.configurations.runtimeClasspath : project.configurations.runtime)
                    }
                }
            }
            into { libraryDir }
            eachFile { FileCopyDetails details ->
                files.add(details.relativePath.getFile(libraryDir))
            }
        }

        fileOperations.sync(new Action<CopySpec>() {
            void execute(CopySpec t) {
                t.duplicatesStrategy = duplicatesStrategy
                project.configure(t, distSpec)
            }
        })

        project.files(files)
    }
}
