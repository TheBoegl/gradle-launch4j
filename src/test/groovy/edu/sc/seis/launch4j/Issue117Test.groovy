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

class Issue117Test extends FunctionalSpecification {
    def 'Check EXCLUDE duplication Strategy'() {
        given:
        buildFile << """
            dependencies {
                implementation 'org.glassfish.jaxb:jaxb-runtime:3.0.2'
                implementation 'com.sun.xml.bind:jaxb-xjc:3.0.2'
            }
            launch4j {
                outfile = 'test.exe'
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').withGradleVersion('7.5.1').build()

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
        process.in.text.trim() == '...'
    }

    def 'Check default duplication Strategy'() {
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
        def result = createAndConfigureGradleRunner('createExe').withGradleVersion('7.5.1').build()

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
        process.in.text.trim() == '...'
    }
}
