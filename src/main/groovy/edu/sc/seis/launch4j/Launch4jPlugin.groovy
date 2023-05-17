/*
 * Copyright (c) 2022 Sebastian Boegl
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
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.internal.file.FileOperations
import org.gradle.internal.os.OperatingSystem
import org.gradle.util.GradleVersion

import javax.inject.Inject
//@CompileStatic
class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_EXTENSION_NAME = LAUNCH4J_PLUGIN_NAME
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
    static final String TASK_RUN_NAME = 'createExe'
    static final String ARTIFACT_VERSION = '3.50'
    static final String LAUNCH4J_BINARY_DIRECTORY = "tmp/launch4j/"

    private FileOperations fileOperations

    @Inject
    Launch4jPlugin(FileOperations fileOperations) {
        this.fileOperations = fileOperations
    }

    @Override
    void apply(Project project) {
        if (GradleVersion.current() < GradleVersion.version("4.9")) {
            throw new GradleException("this plugin version requires gradle 4.9 and newer.\nUse the latest version 2.x release or update gradle.")
        }
        project.extensions.create(LAUNCH4J_EXTENSION_NAME, Launch4jPluginExtension.class, project, fileOperations, project.objects, project.providers)

        def l4jConfig = configureDependencies(project)
        project.tasks.register(TASK_RUN_NAME, Launch4jLibraryTask.class) { task ->
            task.group = LAUNCH4J_GROUP
            task.description = 'Runs the launch4j jar to generate an .exe file'
        }
        project.tasks.register("createAllExecutables", DefaultTask.class) { task ->
            task.group = LAUNCH4J_GROUP
            task.description = 'Runs all tasks that implement DefaultLaunch4jTask'
            task.dependsOn = project.tasks.withType(DefaultLaunch4jTask.class)
        }
        project.tasks.withType(DefaultLaunch4jTask.class).configureEach {it.launch4jBinaryFiles.from(l4jConfig)}
    }

    private static ModuleDependency addDependency(Project project, Configuration configuration, String notation) {
        ModuleDependency dependency = project.dependencies.create(notation) as ModuleDependency
        configuration.dependencies.add(dependency)
        dependency
    }

    static Configuration configureDependencies(final Project project) {
        Configuration binaryConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false)
            .setTransitive(false).setDescription('The launch4j binary configuration for this project.')
        binaryConfig.setCanBeConsumed(false)
        binaryConfig.setCanBeResolved(true)


        if (project.repositories.isEmpty()) {
            project.logger.debug("Adding the mavenCentral repository to retrieve the $LAUNCH4J_PLUGIN_NAME files.")
            project.repositories.mavenCentral()
        }
        binaryConfig.resolutionStrategy.dependencySubstitution{
            all { DependencySubstitution dependency ->
                if (dependency.requested instanceof ModuleComponentSelector && dependency.requested.group == 'net.sf.launch4j' && dependency.requested.module == 'launch4j') {
                    // does not work to add the classifier here, so the user must supply it.
                    dependency.useTarget "net.sf.launch4j:launch4j:${dependency.requested.version}:${workdir()}"
                }
            }
        }
        def l4jArtifact = "net.sf.launch4j:launch4j:${ARTIFACT_VERSION}"
        binaryConfig.defaultDependencies {it.add(project.dependencies.create("${l4jArtifact}:${workdir()}"))}
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
            if ("amd64" == arch || "x86_64" == arch) {
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
        def minor = version[1] as int
        return major < 10 || (major == 10 && minor < 8)
    }
}
