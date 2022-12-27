/*
 * Copyright (c) 2019 Sebastian Boegl
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
import org.gradle.util.GradleVersion
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Launch4jPluginBackwardsCompatibilityTest extends FunctionalSpecification {

    @Unroll
    def 'Running the task to create the executable with gradle #gradleVersion succeeds'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
            }
        """

        File sourceFile = new File(newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World!");
                }
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe').withDebug(false).withGradleVersion(gradleVersion).build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        where:
        // versions prior 2.8 will not allow the classpath injection
        gradleVersion << ['2.14.1', '3.5.1', '4.10.2', '5.6.4', '6.9.3', '7.6', GradleVersion.current().getVersion()].unique()
    }
}
