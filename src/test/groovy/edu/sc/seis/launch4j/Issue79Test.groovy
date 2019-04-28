package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.util.FunctionalSpecification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue79Test extends FunctionalSpecification {

    def 'Check that setting chdir to empty string does not use default value'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                chdir = ''
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;
            
            public class Main {
                public static void main(String[] args) {
                }
            }
        """

        when:
        def result = build('createExe')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = [outfile.path, '--l4j-debug-all'].execute()
        def logfile = new File(projectDir, 'build/launch4j/launch4j.log')
        then:
        process.waitFor() == 0

        logfile.exists()

        def chdir = logfile.readLines().find { String line ->
            line.contains("Working dir:")
        }
        chdir == null
    }

    def 'Check that not setting chdir uses default value'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;
            
            public class Main {
                public static void main(String[] args) {
                }
            }
        """

        when:
        def result = build('createExe')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = [outfile.path, '--l4j-debug-all'].execute()
        def logfile = new File(projectDir, 'build/launch4j/launch4j.log')
        then:
        process.waitFor() == 0

        logfile.exists()

        def chdir = logfile.readLines().find { String line ->
            line.contains("Working dir:")
        }
        chdir != null
    }


}
