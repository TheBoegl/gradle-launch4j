package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.util.FunctionalSpecification
import org.gradle.api.JavaVersion

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue88Test extends FunctionalSpecification {
    def 'verify source compatibility is used as minimum version'() {
        given:
        buildFile << """
            sourceCompatibility = 1.7
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>1.7.0</minVersion>')
    }

    def 'verify toolchain is used as minimum version'() {
        given:
        buildFile << """
            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(8) // use same version as the one building and provided below
                }
            }
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        // disable auto-detect and put the current jdk on the search path as this fails otherwise
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug', '-Porg.gradle.java.installations.auto-detect=false', '-Porg.gradle.java.installations.paths=' + System.getProperty("java.home")).build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>1.8.0</minVersion>')
    }

    def 'verify source compatibility in java plugin is used as minimum version'() {
        given:
        buildFile << """
            java {
                sourceCompatibility = JavaVersion.VERSION_17
            }
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>17.0.0</minVersion>')
    }

    def 'verify minimum version works as expected'() {
        given:
        buildFile << """
            launch4j {
                outfile = 'test.exe'
                jreMinVersion = '1.8.281'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains("<minVersion>1.8.281</minVersion>")
    }

    def 'verify minimum version defaults to current java version'() {
        given:
        buildFile << """
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains("<minVersion>${JavaVersion.current()}.0</minVersion>")
    }

}
