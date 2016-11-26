package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CreateXML
import edu.sc.seis.launch4j.Launch4jPluginExtension
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CreateLaunch4jXMLTask extends DefaultTask {

    @TaskAction
    def void writeXmlConfig() {
        new CreateXML(project).execute(project.getExtensions().getByName('launch4j') as Launch4jPluginExtension)
    }


}
