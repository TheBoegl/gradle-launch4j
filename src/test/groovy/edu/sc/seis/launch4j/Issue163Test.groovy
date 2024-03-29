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

class Issue163Test extends FunctionalSpecification {
    def 'verify that messages work'() {
        given:
        buildFile << """
            launch4j {
                outfile = 'test.exe'
                messagesStartupError = "Failed to start"
                messagesJreNotFoundError = "Error encountered with bundled JRE"
                messagesJreVersionError = "JRE Found, but expected different version"
                messagesInstanceAlreadyExists = "App is already running"
                messagesInstanceAlreadyExists = "Instance already exists"
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').build()

        then:
        result.task(':createExe').outcome == SUCCESS
    }
}
