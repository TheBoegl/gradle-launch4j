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

class Issue152Test extends FunctionalSpecification {
    def 'verify applying the plugin without applying the java plugin works'() {
        given:
        buildFile.text = ''
        buildFile << """
        plugins {
            id 'edu.sc.seis.launch4j'
        }
        launch4j {
            outfile = 'test.exe'
            dontWrapJar = true
            mainClassName = 'com.test.Main'
            classpath = ['./libs']
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
    }

}
