package edu.sc.seis.launch4j

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.internal.os.OperatingSystem

class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_EXTENSION_NAME = LAUNCH4J_PLUGIN_NAME
    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
    static final String TASK_XML_GENERATE_NAME = "generateXmlConfig"
    static final String TASK_LIB_COPY_NAME = "copyL4jLib"
    static final String TASK_L4j_COPY_NAME = "copyL4jBinLib"
    static final String TASK_L4j_UNZIP_NAME = "unzipL4jBin"
    static final String TASK_RUN_NAME = "createExe"
    static final String TASK_RUN_BIN_NAME = "createExeWithBin"
    static final String TASK_RUN_LIB_NAME = "createExeWithJar"
    static final String TASK_LAUNCH4J_NAME = "launch4j"
    static final String ARTIFACT_VERSION = "3.8.0"

    private Project project

    def void apply(Project project) {
        this.project = project
        Configuration defaultConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME).setVisible(false)
                .setTransitive(true).setDescription('The launch4j configuration for this project.')
        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension(project)
        project.extensions.add(LAUNCH4J_EXTENSION_NAME, pluginExtension)

        Configuration binaryConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false)
                .setTransitive(false).setDescription('The launch4j binary configuration for this project.')

        def l4jArtifact = "net.sf.launch4j:launch4j:${ARTIFACT_VERSION}"
        if (project.repositories.isEmpty()) {
            project.logger.lifecycle("Adding the maven central repository to retrieve the $LAUNCH4J_PLUGIN_NAME files.")
            project.repositories.mavenCentral()
        }
        addDependency(defaultConfig, "${l4jArtifact}").exclude(group: 'dsol').exclude(group: 'org.apache.batik')
        OperatingSystem os = OperatingSystem.current()
        if (os.isLinux()) {
            addDependency(binaryConfig, "${l4jArtifact}:workdir-linux")
        } else if (os.isWindows()) {
            addDependency(binaryConfig, "${l4jArtifact}:workdir-win32")
        } else if (os.isMacOsX()) {
            addDependency(binaryConfig, "${l4jArtifact}:workdir-mac")
        }

        /* initialize default tasks */
        Task xmlTask = addCreateLaunch4jXMLTask(pluginExtension)
        Task copyTask = addCopyToLibTask(pluginExtension)
        Task runTask = addRunLaunch4jTask()
        Task runBinaryTask = addRunLaunch4jBinTask(pluginExtension)
        runBinaryTask.dependsOn(copyTask)
        runBinaryTask.inputs.files xmlTask.outputs.files
        runTask.dependsOn(runBinaryTask)
        Task l4jTask = addLaunch4jTask(pluginExtension)
        l4jTask.dependsOn(runTask)

        /* initialize tasks to retrieve and execute the launch4j jar and its dependencies */
        Task copyL4JTask = addCopyLaunch4JToLibTask(pluginExtension)
        Task unzipL4jTask = addUnzipLaunch4JWorkingBinariesTask(pluginExtension)
        copyL4JTask.dependsOn(unzipL4jTask)
        Task runLibTask = addRunLaunch4jLibTask(pluginExtension)
        runLibTask.dependsOn(copyL4JTask)
        runLibTask.dependsOn(copyTask)
        runLibTask.inputs.files xmlTask.outputs.files
        runTask.dependsOn(runLibTask)

        pluginExtension.onSetCopyConfigurable { Object copyConfigurable ->
            copyTask.enabled = false
            def copyTask2 = addCopyToLibTask(pluginExtension)
            project.tasks.each { it ->
                if (it.dependsOn.contains(copyTask)) {
                    it.dependsOn.remove(copyTask)
                    it.dependsOn copyTask2
                }
            }
            copyTask.dependsOn.clear()
            copyTask2.dependsOn copyTask.dependsOn
            xmlTask.dependsOn copyTask2
        }
    }

    private Task addCreateLaunch4jXMLTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_XML_GENERATE_NAME, CreateLaunch4jXMLTask)
        task.description = "Creates XML configuration file used by launch4j to create an windows exe."
        task.group = LAUNCH4J_GROUP
        task.inputs.property("project version", { project.version })
        task.inputs.property("Launch4j extension", { configuration.hashCode() })
        task.configuration = configuration
        return task
    }

    private Task addCopyToLibTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.replace(TASK_LIB_COPY_NAME, Sync)
        task.description = "Copies the project dependency jars in the lib directory."
        task.group = LAUNCH4J_GROUP
        task.with configureDistSpec(configuration)
        task.into { project.file("${-> project.buildDir}/${-> configuration.outputDir}/lib") }
        return task
    }

    private Task addCopyLaunch4JToLibTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_L4j_COPY_NAME, Sync)
        task.description = "Copies the launch4j jars in the bin and bin/lib directories."
        task.group = LAUNCH4J_GROUP
        task.onlyIf { !configuration.externalLaunch4j }
        CopySpec distSpec = project.copySpec {}
        distSpec.with {
            from(project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME))
        }
        task.with distSpec
        File destination = project.file("${-> project.buildDir}/${-> configuration.outputDir}/bin/lib")
        File jarFile = project.file("${destination.parentFile}/launch4j.jar")
        task.outputs.file(jarFile)
        task.into { destination }
        task.doLast {
            project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME).find {
                if (it.name.startsWith("launch4j")) {
                    project.file("${destination}/${it.name}").renameTo(jarFile)
                }
            }
        }
        return task
    }

    private Task addUnzipLaunch4JWorkingBinariesTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_L4j_UNZIP_NAME, Copy)
        task.description = "Unzips the launch4j working binaries in the bin directory."
        task.group = LAUNCH4J_GROUP
        task.onlyIf { !configuration.externalLaunch4j }
        if (!project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME_BINARY).isEmpty()) {
            def workingJar = project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME_BINARY).find { File file -> file.name =~ /launch4j-.*-workdir-.*.jar/ }
            if (!workingJar) {
                throw new Exception("workingdir jar file not found!")
            }
            task.from project.zipTree(workingJar)
            task.includeEmptyDirs = false
            def destination = "${-> project.buildDir}/${-> configuration.outputDir}/bin"
            task.into { "${-> destination}" }

            def jarName = project.file(workingJar).name
            String zipEntryFolder = jarName.lastIndexOf('.').with { it != -1 ? jarName[0..<it] : jarName }
            task.eachFile { FileCopyDetails fcp ->
                // only extract the binaries
                if (fcp.relativePath.pathString.startsWith(zipEntryFolder)) {
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

        return task
    }

    private Task addRunLaunch4jTask() {
        def task = project.tasks.create(TASK_RUN_NAME)
        task.description = "Placeholder task to run launch4j to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        return task
    }


    private Task addRunLaunch4jBinTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_RUN_BIN_NAME, Exec)
        task.description = "Runs the launch4j binary to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        task.onlyIf { configuration.externalLaunch4j }
        task.commandLine "${-> configuration.launch4jCmd}", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        task.workingDir "${-> project.buildDir}/${-> configuration.outputDir}"
        task.inputs.dir("${-> project.buildDir}/${-> configuration.outputDir}/lib")
        task.outputs.file("${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.outfile}")
        task.standardOutput = new ByteArrayOutputStream()
        task.errorOutput = task.standardOutput
        task.ignoreExitValue = true
        task.doLast {
            if (execResult.exitValue != 0) {
                throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${standardOutput.toString()}");
            } else {
                //return value not set in launch4j 3.8.0, so test the outcome by iterating over the expected output files
                outputs.files.each {
                    if (!it.exists()) {
                        throw new GradleException("$it.name not created:\n\t${standardOutput.toString()}")
                    }
                }
            }
        }
        return task
    }

    private Task addRunLaunch4jLibTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_RUN_LIB_NAME, Exec)
        task.description = "Runs the launch4j jar to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        task.onlyIf { !configuration.externalLaunch4j }
        task.commandLine "java", "-jar", "bin/launch4j.jar", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        task.workingDir "${-> project.buildDir}/${-> configuration.outputDir}"
        task.outputs.file("${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.outfile}")
        task.standardOutput = new ByteArrayOutputStream()
        task.errorOutput = task.standardOutput
        task.ignoreExitValue = true
        task.doLast {
            if (execResult.exitValue != 0) {
                throw new GradleException("Launch4J finished with non-zero exit value ${execResult.exitValue}\n${standardOutput.toString()}");
            } else {
                //return value not set in launch4j 3.8.0, so test the outcome by iterating over the expected output files
                outputs.files.each {
                    if (!it.exists()) {
                        throw new GradleException("$it.name not created:\n\t${standardOutput.toString()}")
                    }
                }
            }
        }
        return task
    }

    private Task addLaunch4jTask(Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_LAUNCH4J_NAME)
        task.description = "Placeholder task for tasks relating to creating .exe applications with launch4j"
        task.group = LAUNCH4J_GROUP
        task.outputs.file("${-> project.buildDir}/${-> configuration.outputDir}")
        return task
    }

    private CopySpec configureDistSpec(Launch4jPluginExtension configuration) {
        CopySpec distSpec = project.copySpec {}

        distSpec.with {
            if (configuration.copyConfigurable) {
                from {configuration.copyConfigurable}
            } else
            if (project.plugins.hasPlugin('java')) {
                from(project.tasks[JavaPlugin.JAR_TASK_NAME])
                from(project.configurations.runtime)
            }
        }
        return distSpec
    }

    private ModuleDependency addDependency(Configuration configuration, String notation) {
        ModuleDependency dependency = project.dependencies.create(notation) as ModuleDependency
        configuration.dependencies.add(dependency)
        dependency
    }

    private <T extends Task> T createOrReplaceTask(String name, Class<T> type) {
        if (project.tasks.findByName(name)) {
            return project.tasks.replace(name, type)
        } else {
            return project.tasks.create(name, type)
        }
    }
}





