package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import edu.sc.seis.launch4j.tasks.Launch4jLibraryTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.os.OperatingSystem

class Launch4jPlugin implements Plugin<Project> {

    static final String LAUNCH4J_PLUGIN_NAME = "launch4j"
    static final String LAUNCH4J_GROUP = LAUNCH4J_PLUGIN_NAME

    static final String LAUNCH4J_EXTENSION_NAME = LAUNCH4J_PLUGIN_NAME
    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String LAUNCH4J_CONFIGURATION_NAME_BINARY = 'launch4jBin'
//    static final String TASK_XML_GENERATE_NAME = 'generateXmlConfig'
//    static final String TASK_LIB_COPY_NAME = 'copyL4jLib'
    static final String TASK_RUN_NAME = 'createExe'
//    static final String TASK_RUN_BIN_NAME = 'createExeWithBin'
//    static final String TASK_RUN_LIB_NAME = 'createExeWithJar'
    static final String TASK_LAUNCH4J_NAME = 'launch4j'
    static final String ARTIFACT_VERSION = '3.9'

    private Project project

    @Override
    def void apply(Project project) {
        this.project = project

//        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension()
//        project.extensions.add(LAUNCH4J_EXTENSION_NAME, pluginExtension)

        project.extensions.create(LAUNCH4J_EXTENSION_NAME, Launch4jPluginExtension, project)

//        project.tasks.create(TASK_LIB_COPY_NAME)
//        project.tasks.create(TASK_RUN_NAME)
//        project.tasks.create(TASK_LAUNCH4J_NAME)

        configureDependencies(project)
        applyTasks(project)

        project.afterEvaluate {
//            pluginExtension.afterEvaluate(project)


            /* initialize default tasks */
//            Task xmlTask = addCreateLaunch4jXMLTask(pluginExtension)
//            Task copyTask = addCopyToLibTask(pluginExtension)
//            Task runTask = addRunLaunch4jTask()
//            Task runBinaryTask = addRunLaunch4jBinTask(pluginExtension)
//            runBinaryTask.dependsOn(copyTask)
//            runBinaryTask.inputs.files xmlTask.outputs.files
//            runTask.dependsOn(runBinaryTask)
//            runTask.inputs.files copyTask.outputs.files
//            Task l4jTask = addLaunch4jTask()
//            l4jTask.dependsOn(runTask)

            /* initialize tasks to retrieve and execute the launch4j jar and its dependencies */
//            def runLibTask = project.tasks.getByName(TASK_RUN_LIB_NAME)
//            runLibTask.dependsOn(copyTask)
//            runLibTask.inputs.files xmlTask.outputs.files
//            runLibTask.inputs.files copyTask.outputs.files
//            runTask.dependsOn(TASK_RUN_LIB_NAME)

//            pluginExtension.onSetCopyConfigurable { Object copyConfigurable ->
//                copyTask.enabled = false
//                def copyTask2 = addCopyToLibTask(pluginExtension)
//                project.tasks.each { task ->
//                    if (task.dependsOn.contains(copyTask)) {
//                        task.dependsOn.remove(copyTask)
//                        task.dependsOn copyTask2
//                    }
//                }
//                copyTask.dependsOn.clear()
//                copyTask2.dependsOn copyTask.dependsOn
//                xmlTask.dependsOn copyTask2
//            }
        }
    }

//    private Task addCreateLaunch4jXMLTask(Launch4jPluginExtension configuration) {
//        def task = project.tasks.create(TASK_XML_GENERATE_NAME, CreateLaunch4jXMLTask)
//        task.description = "Creates XML configuration file used by launch4j to create an windows exe."
//        task.group = LAUNCH4J_GROUP
//        task.inputs.property("project version", { "${-> project.version}" })
//        task.inputs.property("Launch4j extension", { configuration })
//        task.configuration = configuration
//        return task
//    }
//
//    private Task addRunLaunch4jTask() {
//        def task = project.tasks.replace(TASK_RUN_NAME)
//        task.description = "Placeholder task to run launch4j to generate an .exe file"
//        task.group = LAUNCH4J_GROUP
//        return task
//    }

    void applyTasks(final Project project) {
        def runLibTask = project.task(TASK_RUN_NAME, type: Launch4jLibraryTask, group: LAUNCH4J_GROUP, description: 'Runs the launch4j jar to generate an .exe file')
//        def runBinTask = project.task(TASK_RUN_BIN_NAME, type: Launch4jExternalTask, group: LAUNCH4J_GROUP, description: 'Runs the launch4j binary to generate an .exe file')
//        def generateXmlTask = project.task(TASK_XML_GENERATE_NAME, type: CreateLaunch4jXMLTask, group: LAUNCH4J_GROUP, description: 'Creates XML configuration file used by launch4j to create an windows exe.')
        if (project.plugins.hasPlugin('java')) {
            runLibTask.dependsOn project.tasks[JavaPlugin.JAR_TASK_NAME]
//            runBinTask.dependsOn project.tasks[JavaPlugin.JAR_TASK_NAME]
        }
        def createAllExecutables = project.task("createAllExecutables", group: LAUNCH4J_GROUP, description: 'Runs all tasks that implements Launch4jLibraryTask')
//        project.afterEvaluate {
            createAllExecutables.dependsOn project.tasks.withType(DefaultLaunch4jTask)
//        }

//        runBinTask.dependsOn(generateXmlTask)
//        runLibTask.dependsOn(generateXmlTask)
        def l4jPlaceholderTask = project.task(TASK_LAUNCH4J_NAME, group: LAUNCH4J_GROUP, description: 'Placeholder task to run launch4j to generate an .exe file')
        l4jPlaceholderTask.dependsOn runLibTask
        l4jPlaceholderTask.doFirst {
            project.logger.warn("The `launch4j` task is deprecated. Use the `createExe` task instead")
        }
    }

//    private Task addLaunch4jTask() {
//        def task = project.tasks.replace(TASK_LAUNCH4J_NAME)
//        task.description = "Placeholder task for tasks relating to creating .exe applications with launch4j"
//        task.group = LAUNCH4J_GROUP
////        task.outputs.file("${-> configuration.outputDir}")
//        return task
//    }

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


    void configureDependencies(final Project project) {

        Configuration defaultConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME).setVisible(false)
                .setTransitive(true).setDescription('The launch4j configuration for this project.')

        Configuration binaryConfig = project.configurations.create(LAUNCH4J_CONFIGURATION_NAME_BINARY).setVisible(false)
                .setTransitive(false).setDescription('The launch4j binary configuration for this project.')


        project.configurations {
            l4j
        }
        if (project.repositories.isEmpty()) {
            project.logger.lifecycle("Adding the maven central repository to retrieve the $LAUNCH4J_PLUGIN_NAME files.")
            project.repositories.mavenCentral()
        }
        def l4jArtifact = "net.sf.launch4j:launch4j:${ARTIFACT_VERSION}"
        project.dependencies {
            l4j "${l4jArtifact}"
            addDependency(defaultConfig, "${l4jArtifact}").exclude(group: 'dsol').exclude(group: 'org.apache.batik')
            l4j 'com.thoughtworks.xstream:xstream:1.4.8'
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





