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

import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import groovy.transform.CompileStatic
import org.gradle.api.GradleException
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
//    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
    static final String TASK_RUN_NAME = 'createExe'
    static final String TASK_LAUNCH4J_NAME = 'launch4j'
    static final String ARTIFACT_VERSION = '3.12'
    static final String LAUNCH4J_BINARY_DIRECTORY = "tmp/launch4j/bin-launch4j-${ARTIFACT_VERSION}"

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
        Configuration binaryConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false)
            .setTransitive(false).setDescription('The launch4j binary configuration for this project.')


        if (project.repositories.isEmpty()) {
            project.logger.debug("Adding the jcenter repository to retrieve the $LAUNCH4J_PLUGIN_NAME files.")
            project.repositories.jcenter()
        }
        def l4jArtifact = "net.sf.launch4j:launch4j:${ARTIFACT_VERSION}"
        project.dependencies {
            addDependency(binaryConfig, "${l4jArtifact}:${workdir()}")
        }
    }

    static String workdir() {
        OperatingSystem os = OperatingSystem.current()
        if (os.isWindows()) {
            return 'workdir-win32'
        } else if (os.isMacOsX()) {
            if(isBelowMacOsX108()) {
                throw new GradleException('Mac OS X below version 10.8 (Mountain Lion) is not supported by launch4j version 3.11 and later. Please use an earlier version of this plugin, e.g. 2.3.0.')
            }
            return 'workdir-mac'
        } else if (os.isLinux() || os.isUnix()) { // isUnix will also match MacOs, hence, call it as last resort
            String arch = System.getProperty("os.arch")
            if ("amd64".equals(arch) || "x86_64".equals(arch)) {
                return 'workdir-linux64'
            } else {
                return 'workdir-linux'
            }
        }
        return ''
    }

    static boolean isBelowMacOsX108() {
        def version = System.getProperty("os.version").split('\\.')
        def major = version[0] as int
        def minor = version[0] as int
        return major < 10 || (major == 10 && minor < 8)
    }
}
