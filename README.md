[![Build status](https://ci.appveyor.com/api/projects/status/xscd7594tneg721r/branch/master?svg=true)](https://ci.appveyor.com/project/TheBoegl/gradle-launch4j/branch/master)

Introduction
============

The gradle-launch4j plugin uses [launch4j](http://launch4j.sourceforge.net/) to create windows .exe files for java applications.

Tasks
-----
There are 8 tasks:

  * generateXmlConfig - Creates XML configuration file used by launch4j.
  * copyL4jLib - Copies the project dependency jars in the lib directory.
  * copyL4jBinLib - Copies the launch4j jars in the bin and bin/lib directories.
  * unzipL4jBin - Unzips the launch4j working binaries in the bin directory.
  * createExeWithBin - Runs the launch4j binary to generate an .exe file.
  * createExeWithJar - Runs the launch4j jar to generate an .exe file.
  * createExe - Backward compatible task to generate an .exe file.
  * **launch4j** - Placeholder task that depends on the above. *Execute this task to generate an executable.*

Launch4j no longer needs to be installed separately, but if you want, you can still use it from the *PATH* with the configuration parameter `externalLaunch4j = true`.

Configuration
-------------

The configuration follows the structure of the launch4j xml file. See the launch4j documentation for the meanings. The gradle-launch4j plugin tries to pick sensible defaults based on the project. The only required
value is the mainClassName.

An example configuration within your build.gradle for use in all Gradle versions might look like:

    buildscript {
      repositories {
        maven {
          url 'https://plugins.gradle.org/m2/'
        }
      }
      dependencies {
        classpath 'gradle.plugin.edu.sc.seis.gradle:launch4j:1.6.1'
      }
    }

    repositories {
      mavenCentral()
    }

    apply plugin: 'java'
    apply plugin: 'edu.sc.seis.launch4j'

    launch4j {
      mainClassName = 'com.example.myapp.Start'
      icon = 'icons/myApp.ico'
    }

The same script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:

    apply plugin: 'java'

    plugins {
      id 'edu.sc.seis.launch4j' version '1.6.1'
    }

    launch4j {
      mainClassName = 'com.example.myapp.Start'
      icon = 'icons/myApp.ico'
    }


If no repository is configured before applying this plugin the *Maven central* repository will be added to the project.

See the [Gradle User guide](http://gradle.org/docs/current/userguide/custom_plugins.html#customPluginStandalone) for more information on how to use a custom plugin.

The values configurable within the launch4j extension along with their defaults are:

 *    String launch4jCmd = "launch4j"
 *    String outputDir = "launch4j"
 *    String libraryDir = "lib"
 *    boolean externalLaunch4j = false
 *    Object copyConfigurable

&nbsp;  

 *    String xmlFileName = "launch4j.xml"
 *    String mainClassName
 *    boolean dontWrapJar = false
 *    String headerType = "gui"
 *    String jar = "lib/"+project.tasks[JavaPlugin.JAR_TASK_NAME].archiveName or "", if the JavaPlugin is not loaded
 *    String outfile = project.name+'.exe'
 *    String errTitle = ""
 *    String cmdLine = ""
 *    String chdir = '.'
 *    String priority = 'normal'
 *    String downloadUrl = "http://java.com/download"
 *    String supportUrl = ""
 *    boolean stayAlive = false
 *    boolean restartOnCrash = false
 *    String manifest = ""
 *    String icon = ""
 *    String version = project.version
 *    String textVersion = project.version
 *    String copyright = "unknown"
 *    String companyName = ""
 *    String description = project.name
 *    String productName = project.name
 *    String internalName = project.name
 *    String trademarks
 *    String opt = ""
 *    String bundledJrePath
 *    boolean bundledJre64Bit = false
 *    boolean bundledJreAsFallback = false
 *    String jreMinVersion = project.targetCompatibility or the current java version, if the property is not set
 *    String jreMaxVersion
 *    String jdkPreference = "preferJre"
 *    String jreRuntimeBits = "64/32"
 *    String mutexName
 *    String windowTitle
 *    String messagesStartupError
 *    String messagesBundledJreError
 *    String messagesJreVersionError
 *    String messagesLauncherError
 *    Integer initialHeapSize
 *    Integer initialHeapPercent
 *    Integer maxHeapSize
 *    Integer maxHeapPercent
 *    String splashFileName
 *    boolean splashWaitForWindows = true
 *    Integer splashTimeout = 60
 *    boolean splashTimeoutError = true

Take a look at the [Launch4j documentation](http://launch4j.sourceforge.net/docs.html#Configuration_file) for valid options.

# Configurable input configuration

In order to configure the input of the *copyL4jLib* task set the `copyConfigurable` property.
The following example shows how to use this plugin hand in hand with the shadow plugin:

    launch4j {
        outfile = 'TestMain.exe'
        mainClassName = project.mainClassName
        copyConfigurable = project.tasks.shadowJar.outputs.files
        jar = "lib/${project.tasks.shadowJar.archiveName}"
    }

If you use the outdated fatJar plugin the following configuration correctly wires the execution graph:

    fatJar {
        classifier 'fat'
        manifest {
            attributes 'Main-Class': project.mainClassName
        }
    }
    
    copyL4jLib.dependsOn fatJar
    fatJarPrepareFiles.dependsOn jar
    
    launch4j {
        outfile = 'TestMain.exe'
        mainClassName = project.mainClassName
        copyConfigurable = project.tasks.fatJar.outputs.files
        jar = "lib/${project.tasks.fatJar.archiveName}"
    }

