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
import spock.lang.Timeout

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Java8Test extends FunctionalSpecification {
    @Timeout(60)
    def 'verify runs with java 8'() {
        given:
        buildFile << """
                java.sourceCompatibility = JavaVersion.VERSION_1_8
                java.targetCompatibility = JavaVersion.VERSION_1_8
                launch4j {
                    outfile = 'test.exe'
                    bundledJrePath = '%JAVA_HOME%;%JAVA_HOME_8%'
                    jreMinVersion = '1.8.0'
                    jreMaxVersion = '1.8.0_999'
                }
            """

        when:
        def result = createAndConfigureGradleRunner('createExe').build()

        then:
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
