package edu.sc.seis.launch4j;

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin

import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.*

class Launch4jPlugin implements Plugin<Project> {
    static final String LAUNCH4J_CONFIGURATION_NAME = 'launch4j'
    static final String TASK_XML_GENERATE_NAME = "generateXmlConfig"
    Launch4jPluginExtension pluginConvention;
    
    def void apply(Project project) {
        project.plugins.apply(JavaPlugin)
        project.configurations.add(LAUNCH4J_CONFIGURATION_NAME).setVisible(false).setTransitive(true)
        .setDescription('The launch4j configuration for this project.')
        Launch4jPluginExtension pluginExtension = new Launch4jPluginExtension()
        pluginExtension.initExtensionDefaults(project)
        project.extensions.launch4j = pluginExtension
        addCreateLaunch4jXMLTask(project)
        project.task('hello') << {
            println "Hello from the Launch4jPlugin"
        }
    }
    // copy from Application Plugin
    
    private void addCreateLaunch4jXMLTask(Project project) {
        def xmlGen = project.tasks.add(TASK_XML_GENERATE_NAME, CreateLaunch4jXMLTask)
        xmlGen.description = "Creates XML configuration file used by launch4j to create an windows exe."
    }
    
}





