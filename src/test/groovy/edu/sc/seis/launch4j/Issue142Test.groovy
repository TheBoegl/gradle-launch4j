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

class Issue142Test extends FunctionalSpecification {
    def 'verify that the configuration cache works'() {
        given:
        buildFile << """
            launch4j {
                outfile = 'test.exe'
            }
        """


        // the TestKit is not yet implemented with the configuration cache, so we convert the error to a warning and check for its presence below.
        def gradleRunner = createAndConfigureGradleRunner('--configuration-cache', '--configuration-cache-problems=warn', 'createExe').withGradleVersion('8.1.1')
        when:
        gradleRunner.build()

        and:
        def result = gradleRunner.build()

        then:
        result.output.contains('1 problem was found reusing the configuration cache.\n' +
            '- Gradle runtime: support for using a Java agent with TestKit builds is not yet implemented with the configuration cache.')
        result.output.contains('Reusing configuration cache.')
    }
}
