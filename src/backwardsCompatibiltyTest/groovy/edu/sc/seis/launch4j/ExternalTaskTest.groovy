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

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
/**
 * Test case to check jar task is not in the task graph if shadowjar is called.
 */
class ExternalTaskTest extends FunctionalSpecification {


    def 'verify that the external task can be configured with gradle 4'() {
        given:
        buildFile << """

            ext {
                mainTestClassName = 'com.test.app.Main'
            }

            launch4j {
                outfile = 'test.exe'
                mainClassName = mainTestClassName
            }

            tasks.register('externalExe', edu.sc.seis.launch4j.tasks.Launch4jExternalTask) {
                outfile = 'externalTask.exe'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').withGradleVersion('4.10.3').build()

        then:
        result.task(':createExe').outcome == SUCCESS

        when:
        def fail = createAndConfigureGradleRunner('externalExe').withGradleVersion('4.10.3').buildAndFail()

        then:
        fail.task(':externalExe').outcome == FAILED
    }

    def 'verify that the external task can be configured with gradle 6'() {
        given:
        buildFile << """

            ext {
                mainTestClassName = 'com.test.app.Main'
            }

            launch4j {
                outfile = 'test.exe'
                mainClassName = mainTestClassName
            }

            tasks.register('externalExe', edu.sc.seis.launch4j.tasks.Launch4jExternalTask) {
                outfile = 'externalTask.exe'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').withGradleVersion('6.9.4').build()

        then:
        result.task(':createExe').outcome == SUCCESS

        when:
        def fail = createAndConfigureGradleRunner('externalExe', '--stacktrace').withGradleVersion('6.9.4').buildAndFail()

        then:
        fail.task(':externalExe').outcome == FAILED
    }
}
