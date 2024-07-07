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

class Issue169Test extends FunctionalSpecification {
    def 'verify setJarTask works with registered task'() {
        given:
        buildFile << """
        plugins {
            id "application"
        }
        def releaseDir='releaseDir'

        def myJarTask = tasks.register('buildJar', Jar) {
            description 'Foo in releaseDir'
            from sourceSets.main.output
            manifest {
                attributes 'Main-Class': application.mainClass
                attributes 'Class-Path': './config/'
            }
            archiveBaseName = 'Foo'
            destinationDirectory = new File(releaseDir)
        }

        launch4j {
            jarTask = myJarTask
            dontWrapJar = true
            outfile = "Foo.exe"
            bundledJrePath = "./jre21"
            requires64Bit  = true
        }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').build()

        then:
        result.task(':createExe').outcome == SUCCESS
    }

    def 'verify setJarTask works with named task'() {
        given:
        buildFile << """
        plugins {
            id "application"
        }
        def releaseDir='releaseDir'

        tasks.register('buildJar', Jar) {
            description 'Foo in releaseDir'
            from sourceSets.main.output
            manifest {
                attributes 'Main-Class': application.mainClass
                attributes 'Class-Path': './config/'
            }
            archiveBaseName = 'Foo'
            destinationDirectory = new File(releaseDir)
        }

        launch4j {
            jarTask = tasks.named('buildJar')
            dontWrapJar = true
            outfile = "Foo.exe"
            bundledJrePath = "./jre21"
            requires64Bit  = true
        }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').build()

        then:
        result.task(':createExe').outcome == SUCCESS
    }
}
