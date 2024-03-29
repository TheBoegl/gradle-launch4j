/*
 * Copyright (c) 2023 Sebastian Boegl
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
        process.in.text.trim() == '...'
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
        result.tasks.isEmpty()
        result.output.contains('launch4j binary jar')
        result.output.contains('file not found!')
        result.output.contains('Use the correct classifier for this platform.')
    }

}
