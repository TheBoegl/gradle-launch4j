/*
 * Copyright (c) 2025 Sebastian Boegl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

        when:
        def result = build('createExe')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        if (System.getenv('OS').contains('Windows')) {
            when:
            def process = [outfile.path, '--l4j-debug-all'].execute()
            def logfile = new File(projectDir, 'build/launch4j/launch4j.log')
            then:
            process.waitFor() == 0
            process.in.text.trim() == '...'

            logfile.exists()

            def chdir = logfile.readLines().find { String line ->
                line.contains("Working dir:")
            }
            chdir == null
        }
    }

    def 'Check that not setting chdir uses default value'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
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

        if (System.getenv('OS').contains('Windows')) {
            when:
            def process = [outfile.path, '--l4j-debug-all'].execute()
            def logfile = new File(projectDir, 'build/launch4j/launch4j.log')
            then:
            process.waitFor() == 0
            process.in.text.trim() == '...'

            logfile.exists()

            def chdir = logfile.readLines().find { String line ->
                line.contains("Working dir:")
            }
            chdir != null
        }
    }


}
