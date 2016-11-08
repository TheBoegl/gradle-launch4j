package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.CreateXML
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CreateLaunch4jXMLTask extends DefaultTask {

    @TaskAction
    def void writeXmlConfig() {
        new CreateXML(project).execute(project.launch4j)
    }


}
