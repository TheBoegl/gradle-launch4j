package edu.sc.seis.launch4j

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction;


class ExecLaunch4JTask extends DefaultTask {


    @TaskAction
    def void runLaunch4J() {
        Launch4jPluginExtension configuration = project.launch4j
        Process procEcho = [project.launch4j.launch4jCmd, configuration.getXmlOutFileForProject(project).getCanonicalPath()].execute(null, configuration.getXmlOutFileForProject(project).parentFile)
        procEcho.consumeProcessErrorStream(System.err)
        procEcho.consumeProcessOutputStream(System.out)
        if (procEcho.waitFor() != 0) {
            throw new RuntimeException(project.launch4j.launch4jCmd+' exec failed')
        }
        println ""
    }

}
