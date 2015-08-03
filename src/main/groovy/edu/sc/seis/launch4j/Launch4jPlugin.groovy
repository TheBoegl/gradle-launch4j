package edu.sc.seis.launch4j

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync

class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
    static final String TASK_XML_GENERATE_NAME = "generateXmlConfig"
    static final String TASK_LIB_COPY_NAME = "copyL4jLib"
    static final String TASK_L4j_COPY_NAME = "copyL4jBinaryLibs"
    static final String TASK_L4j_UNZIP_NAME = "unzipL4jWorkingBinaries"
    static final String TASK_RUN_NAME = "createExe"
    static final String TASK_RUN_LIB_NAME = "createExeFromJar"
    static final String TASK_LAUNCH4J_NAME = "launch4j"
    Launch4jPluginExtension pluginConvention;

    def void apply(Project project) {
        def config = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The launch4j configuration for this project.')
        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension()
        pluginExtension.initExtensionDefaults(project)
        project.extensions.launch4j = pluginExtension
        def l4jArtifact = 'net.sf.launch4j:launch4j:latest.release'
        project.dependencies.add(LAUNCH4J_CONFIGURATION_NAME, "${l4jArtifact}")

        project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false).setTransitive(false)
                .setDescription('The launch4j binary configuration for this project.')
        switch (OS.CURRENT) {
            case OS.Linux:
                project.dependencies.add(LAUNCH4J_CONFIGURATION_NAME_BINARY, "${l4jArtifact}:workdir-linux")
                break
            case OS.Windows:
                project.dependencies.add(LAUNCH4J_CONFIGURATION_NAME_BINARY, "${l4jArtifact}:workdir-win32")
                break
            case OS.MacOsX:
                project.dependencies.add(LAUNCH4J_CONFIGURATION_NAME_BINARY, "${l4jArtifact}:workdir-mac")
                break
        }
        config.exclude(group:'dsol')
        config.exclude(group:'org.apache.batik')
        Task xmlTask = addCreateLaunch4jXMLTask(project, pluginExtension)
        Task copyTask = addCopyToLibTask(project, pluginExtension)
        Task copyL4JTask = addCopyLaunch4JToLibTask(project, pluginExtension)
        Task unzipL4jTask = addUnzipLaunch4JWorkingBinariesTask(project, pluginExtension)
        copyL4JTask.dependsOn(unzipL4jTask)
        Task runTask = addRunLauch4jTask(project, pluginExtension)
        runTask.dependsOn(copyTask)
        runTask.dependsOn(copyL4JTask)
        runTask.dependsOn(xmlTask)
        Task runLibTask = addRunLauch4jLibTask(project, pluginExtension)
        runLibTask.dependsOn(copyTask)
        runLibTask.dependsOn(copyL4JTask)
        runLibTask.dependsOn(xmlTask)
        runTask.dependsOn(runLibTask)
        Task l4jTask = addLaunch4jTask(project, pluginExtension)
        l4jTask.dependsOn(runTask)
    }

    private Task addCreateLaunch4jXMLTask(Project project, Launch4jPluginExtension configuration) {
        Task task = project.tasks.create(TASK_XML_GENERATE_NAME, CreateLaunch4jXMLTask)
        task.description = "Creates XML configuration file used by launch4j to create an windows exe."
        task.group = LAUNCH4J_GROUP
        task.inputs.property("project version", project.version)
        task.inputs.property("Launch4j extension", configuration)
        task.outputs.file(project.file(configuration.xmlFileName))
        task.configuration = configuration
        return task
    }

    private Task addCopyToLibTask(Project project, Launch4jPluginExtension configuration) {
        Sync task = project.tasks.create(TASK_LIB_COPY_NAME, Sync)
        task.description = "Copies the project dependency jars in the lib directory."
        task.group = LAUNCH4J_GROUP
        task.with configureDistSpec(project)
        task.into { project.file("${project.buildDir}/${configuration.outputDir}/lib") }
        return task
    }

    private Task addCopyLaunch4JToLibTask(Project project, Launch4jPluginExtension configuration) {
        Sync task = project.tasks.create(TASK_L4j_COPY_NAME, Sync)
        task.description = "Copies the launch4j jars in the bin and bin/lib directories."
        task.group = LAUNCH4J_GROUP
        task.onlyIf { !configuration.externalLaunch4j }
        CopySpec distSpec = project.copySpec {}
        distSpec.with {
            from(project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME))
        }
        task.with distSpec
        File destination = project.file("${->project.buildDir}/${->configuration.outputDir}/bin/lib")
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

    private Task addUnzipLaunch4JWorkingBinariesTask(Project project, Launch4jPluginExtension configuration) {
        Copy task = project.tasks.create(TASK_L4j_UNZIP_NAME, Copy)
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
                } else {
                    fcp.exclude()
                }
            }
        }

        return task
    }

    private Task addRunLauch4jTask(Project project, Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_RUN_NAME, Exec)
        task.description = "Runs launch4j to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        task.onlyIf { configuration.externalLaunch4j }
        task.commandLine "${-> configuration.launch4jCmd}", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        task.workingDir "${->project.buildDir}/${->configuration.outputDir}"
        return task
    }

    private Task addRunLauch4jLibTask(Project project, Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_RUN_LIB_NAME, Exec)
        task.description = "Runs the launch4j jar to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        task.onlyIf { !configuration.externalLaunch4j }
        task.commandLine "java", "-jar", "bin/launch4j.jar", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        task.workingDir "${->project.buildDir}/${->configuration.outputDir}"
        return task
    }
    
    private Task addLaunch4jTask(Project project, Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_LAUNCH4J_NAME)
        task.description = "Placeholder task for tasks relating to creating .exe applications with launch4j"
        task.group = LAUNCH4J_GROUP
        task.outputs.file("${-> project.buildDir}/${-> configuration.outputDir}")
        return task
    }

    private CopySpec configureDistSpec(Project project) {
        CopySpec distSpec = project.copySpec {}
        distSpec.with {
            if (project.plugins.hasPlugin('java')) {
                from(project.tasks[JavaPlugin.JAR_TASK_NAME])
                from(project.configurations.runtime)
            }
        }

        return distSpec
    }
}





