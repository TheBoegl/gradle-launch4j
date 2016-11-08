package edu.sc.seis.launch4j

import org.gradle.api.Project
import org.gradle.api.file.FileCopyDetails
import org.gradle.api.file.RelativePath

class ExtractLibraries {
    public static final String LAUNCH4J_BINARY_DIRECTORY = "bin-launch4j-${Launch4jPlugin.ARTIFACT_VERSION}"
    Project project

    ExtractLibraries(Project project) {
        this.project = project
    }

    void execute(File outputDir) {
        if (!project.configurations.getByName(Launch4jPlugin.LAUNCH4J_CONFIGURATION_NAME_BINARY).isEmpty()) {
            def workingJar = project.configurations.getByName(Launch4jPlugin.LAUNCH4J_CONFIGURATION_NAME_BINARY).find { File file -> file.name =~ /launch4j-.*-workdir-.*.jar/ }
            if (!workingJar) {
                throw new Exception("workingdir jar file not found!")
            }
            def destination = new File(outputDir, LAUNCH4J_BINARY_DIRECTORY)
            def jarName = project.file(workingJar).name
            String zipEntryFolder = jarName.lastIndexOf('.').with { it != -1 ? jarName[0..<it] : jarName }
            def copyOptions = {
                from { project.zipTree(workingJar) }
                includeEmptyDirs = false
                into { destination }
                eachFile { FileCopyDetails fcp ->
                    // only extract the binaries
                    if (fcp.relativePath.pathString.startsWith(zipEntryFolder)) {
                        // remap the file to the root
                        def segments = fcp.relativePath.segments
                        def pathSegments = segments[1..-1] as String[]
                        fcp.relativePath = new RelativePath(!fcp.file.isDirectory(), pathSegments)
                        fcp.mode = 0755
                    } else {
                        fcp.exclude()
                    }
                }
            }
            project.copy(copyOptions)
        }

        def l4jConfig = project.configurations.getByName(Launch4jPlugin.LAUNCH4J_CONFIGURATION_NAME)
        File destination = new File(outputDir, LAUNCH4J_BINARY_DIRECTORY + "/lib")
        def distSpec = {
            with {
                from(l4jConfig)
            }
            into { destination }
        }
        project.copy(distSpec)
        File jarFile = project.file("${destination}/launch4j-${Launch4jPlugin.ARTIFACT_VERSION}.jar")
        jarFile.renameTo(project.file("${destination.parentFile}/launch4j-${Launch4jPlugin.ARTIFACT_VERSION}.jar"))
    }
}
