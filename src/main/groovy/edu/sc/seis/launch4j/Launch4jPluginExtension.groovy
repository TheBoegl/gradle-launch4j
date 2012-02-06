
package edu.sc.seis.launch4j

import java.io.File;

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin;


class Launch4jPluginExtension implements Serializable {
    
    String launch4jCmd = "launch4j"

    String outputDir = "launch4j"
    
    String xmlFileName = "launch4j.xml"
    
    String mainClassName
    
    boolean dontWrapJar = false
    
    String headerType = "gui"
    
    String jar
    
    String outfile 
    
    String errTitle = ""
    
    boolean cmdLine = false
    
    String chdir = '.'
    
    String priority = 'normal'
    
    String downloadUrl = ""
    
    String supportUrl = ""
    
    boolean customProcName = false
    
    boolean stayAlive = false
    
    String manifest = ""
    
    String icon = ""
    
    String version = ""
    
    String copyright = "unknown"
    
    String opt = ""
    
    public File getXmlOutFileForProject(Project project) {
        return project.file("${project.buildDir}/${outputDir}/${xmlFileName}")
    }
    
    void initExtensionDefaults(Project project) {
        outfile = new File(project.name+'.exe')
        jar = "lib/"+project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files.getSingleFile().name
        version = project.version
    }
}
