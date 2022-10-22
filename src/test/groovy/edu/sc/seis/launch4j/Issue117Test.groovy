package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.util.FunctionalSpecification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue117Test extends FunctionalSpecification {
    def 'Check duplication Strategy'() {
        given:
        buildFile << """
            dependencies {
                implementation 'org.glassfish.jaxb:jaxb-runtime:3.0.2'
                implementation 'com.sun.xml.bind:jaxb-xjc:3.0.2'
            }
            launch4j {
                outfile = 'test.exe'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').withGradleVersion(gradleVersion).build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0

        where:
        // versions prior 2.8 will not allow the classpath injection
        gradleVersion << ['7.5.1']
    }
}
