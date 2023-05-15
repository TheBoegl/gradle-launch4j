package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.util.FunctionalSpecification

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue144Test extends FunctionalSpecification {
    def 'verify another launch4j version can be used'() {
        given:
        buildFile << """
            dependencies {
                launch4jBin 'net.sf.launch4j:launch4j:3.50:workdir-win32'
            }
            launch4j {
                outfile = 'test.exe'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').build()

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
    }

    def 'verify another version without classifier fails'() {
        given:
        buildFile << """
            dependencies {
                launch4jBin 'net.sf.launch4j:launch4j:3.12'
            }
            launch4j {
                outfile = 'test.exe'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').buildAndFail()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == FAILED
    }

}
