
package edu.sc.seis.launch4j

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin;


class Launch4jPluginExtension {
    
    File outputDir
    
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
    
    
    void initExtensionDefaults(Project project) {
        outfile = new File(project.name+'.exe')
        jar = project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files.getSingleFile()
        version = project.version
    }
}
