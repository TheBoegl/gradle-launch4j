
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin

import groovy.xml.MarkupBuilder
import org.custommonkey.xmlunit.*

class Launch4jPlugin implements Plugin<Project> {

    def void apply(Project project) {
        project.task('hello') << {
            println "Hello from the GreetingPlugin"
        }
        project.task('writeConfig') << {
            println  writeXmlConfig(project)
        }
    }
    
    def void writeXmlConfig(Project project) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.launch4jConfig() {
            dontWrapJar() {false}
            headerType() {gui}
            jar() {'build\\explode\\lib\\gee-2.1.5beta1.jar'}
            outfile() {'build\\explode\\gee.exe'}
            errTitle() {}
            cmdLine() {}
            chdir() {'.'}
            priority() {normal}
            downloadUrl() {'http://java.com/download'}
            supportUrl() {}
            customProcName() {false}
            stayAlive() {false}
            manifest() {}
            icon() {}
            classPath() {
               mainClass() {project.launch4j.mainclass}
               cp() {}
            }
        }
        
    }
}





