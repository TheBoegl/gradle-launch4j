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
/**
 * Test case to check that the variables are settable.
 */
class Issue72Test extends FunctionalSpecification {


    def 'Check that JRE 10 is allowed in maxVersion'() {
        given:
        buildFile << """
            java.sourceCompatibility = JavaVersion.VERSION_1_8
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                jreMinVersion = "1.8.0"
                jreMaxVersion = '10.999'
                bundledJrePath = '%JAVA_HOME%;%JAVA_HOME_8%'
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
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == '...'
    }
}
