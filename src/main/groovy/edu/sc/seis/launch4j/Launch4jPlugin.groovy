package edu.sc.seis.launch4j

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync

class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String TASK_XML_GENERATE_NAME = "generateXmlConfig"
    static final String TASK_LIB_COPY_NAME = "copyL4jLib"
    static final String TASK_L4j_COPY_NAME = "copyL4jBinaryLibs"
    static final String TASK_RUN_NAME = "createExe"
    static final String TASK_LAUNCH4J_NAME = "launch4j"
    Launch4jPluginExtension pluginConvention;

    def void apply(Project project) {
        def config = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
                .setDescription('The launch4j configuration for this project.')
        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension()
        pluginExtension.initExtensionDefaults(project)
        project.extensions.launch4j = pluginExtension
        project.dependencies.add(LAUNCH4J_CONFIGURATION_NAME, 'net.sf.launch4j:launch4j:3.8.0')
        config.exclude(group:'dsol')
        config.exclude(group:'org.apache.batik')
        Task xmlTask = addCreateLaunch4jXMLTask(project, pluginExtension)
        Task copyTask = addCopyToLibTask(project, pluginExtension)
        Task copyL4JTask = addCopyLaunch4JToLibTask(project, pluginExtension)
        Task runTask = addRunLauch4jTask(project, pluginExtension)
        runTask.dependsOn(copyTask)
        runTask.dependsOn(copyL4JTask)
        runTask.dependsOn(xmlTask)
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
        CopySpec distSpec = project.copySpec {}
        distSpec.with {
            from(project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME))
        }
        task.with distSpec
        File destination = project.file("${project.buildDir}/${configuration.outputDir}/bin/lib")
        task.into { destination }
        task.doLast {
            project.configurations.getByName(LAUNCH4J_CONFIGURATION_NAME).find { if (it.name.startsWith("launch4j")) {
                project.file("${destination}/${it.name}").renameTo(project.file("${destination.parentFile}/launch4j.jar"))
            } }
        }
        return task
    }

    private Task addRunLauch4jTask(Project project, Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_RUN_NAME, Exec)
        task.description = "Runs launch4j to generate an .exe file"
        task.group = LAUNCH4J_GROUP
        if (configuration.externalLaunch4j) {
            task.commandLine "${-> configuration.launch4jCmd}", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        } else {
            task.commandLine "java", "-jar", "bin/launch4j.jar", "${-> project.buildDir}/${-> configuration.outputDir}/${-> configuration.xmlFileName}"
        }
        task.workingDir "${->project.buildDir}/${->configuration.outputDir}"
        return task
    }
    
    private Task addLaunch4jTask(Project project, Launch4jPluginExtension configuration) {
        def task = project.tasks.create(TASK_LAUNCH4J_NAME)
        task.description = "Placeholder task for tasks relating to creating .exe applications with launch4j"
        task.group = LAUNCH4J_GROUP
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





