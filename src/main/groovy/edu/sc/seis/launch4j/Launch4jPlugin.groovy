/*
 * Copyright (c) 2017 Sebastian Boegl
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

import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.os.OperatingSystem

import javax.inject.Inject

@CompileStatic
class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_EXTENSION_NAME = LAUNCH4J_PLUGIN_NAME
    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
    static final String TASK_RUN_NAME = 'createExe'
    static final String TASK_LAUNCH4J_NAME = 'launch4j'
    static final String ARTIFACT_VERSION = '3.9'

    private Project project
    private FileOperations fileOperations

    @Inject
    public Launch4jPlugin(FileOperations fileOperations) {
        this.fileOperations = fileOperations
    }

    @Override
    void apply(Project project) {
        this.project = project
        project.extensions.create(LAUNCH4J_EXTENSION_NAME, Launch4jPluginExtension, project, fileOperations)

        configureDependencies(project)
        applyTasks(project)
    }

    void applyTasks(final Project project) {
        def runLibTask = project.task(TASK_RUN_NAME, type: Launch4jLibraryTask, group: LAUNCH4J_GROUP, description: 'Runs the launch4j jar to generate an .exe file')
        def createAllExecutables = project.task("createAllExecutables", group: LAUNCH4J_GROUP, description: 'Runs all tasks that implements DefaultLaunch4jTask')
        createAllExecutables.dependsOn project.tasks.withType(DefaultLaunch4jTask)

        def l4jPlaceholderTask = project.task(TASK_LAUNCH4J_NAME, group: LAUNCH4J_GROUP, description: 'Deprecated placeholder task to run launch4j to generate an .exe file')
        l4jPlaceholderTask.dependsOn runLibTask
        l4jPlaceholderTask.doFirst {
            project.logger.warn("The `${TASK_LAUNCH4J_NAME}` task is deprecated. Use the `${TASK_RUN_NAME}` task instead")
        }
    }

    private ModuleDependency addDependency(Configuration configuration, String notation) {
        ModuleDependency dependency = project.dependencies.create(notation) as ModuleDependency
        configuration.dependencies.add(dependency)
        dependency
    }

    void configureDependencies(final Project project) {
        Configuration defaultConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME).setVisible(false)
            .setTransitive(true).setDescription('The launch4j configuration for this project.')

        Configuration binaryConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false)
            .setTransitive(false).setDescription('The launch4j binary configuration for this project.')


        if (project.repositories.isEmpty()) {
            project.logger.debug("Adding the jcenter repository to retrieve the $LAUNCH4J_PLUGIN_NAME files.")
            project.repositories.jcenter()
        }
        def l4jArtifact = "net.sf.launch4j:launch4j:${ARTIFACT_VERSION}"
        project.dependencies {
            addDependency(defaultConfig, "${l4jArtifact}").exclude(group: 'dsol').exclude(group: 'org.apache.batik')
            addDependency(defaultConfig, 'com.thoughtworks.xstream:xstream:1.4.9')
            OperatingSystem os = OperatingSystem.current()
            if (os.isWindows()) {
                addDependency(binaryConfig, "${l4jArtifact}:workdir-win32")
            } else if (os.isMacOsX()) {
                addDependency(binaryConfig, "${l4jArtifact}:workdir-mac")
            } else if (os.isLinux() || os.isUnix()) { // isUnix will also match MacOs, hence, call it as last resort
                addDependency(binaryConfig, "${l4jArtifact}:workdir-linux")
            }
        }
    }
}





