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

class Issue176Test extends FunctionalSpecification {
    def 'verify we use settings repository'() {

        given:
        settingsFile << """
        dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {
                mavenCentral()
            }
        }
        rootProject.name = 'Issue176'
        """
        buildFile << """
        launch4j {
            mainClassName = 'com.test.app.Main'
            outfile = 'Test.exe'
        }
        """

        when:
        def home = testProjectDir.resolve("gradle_home").toFile()
        def result = createAndConfigureGradleRunner('createExe').withTestKitDir(home).build()

        then:
        result.task(':createExe').outcome == SUCCESS
    }

    def 'verify we fail if there are no settings repositories and we are forbidden to add one'() {

        given:
        settingsFile << """
        dependencyResolutionManagement {
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        }
        rootProject.name = 'Issue176'
        """
        buildFile << """
        launch4j {
            mainClassName = 'com.test.app.Main'
            outfile = 'Test.exe'
        }
        """

        when:
        def fail = createAndConfigureGradleRunner('createExe').buildAndFail()

        then:
        fail.output.contains("Cannot resolve external dependency")
    }

}
