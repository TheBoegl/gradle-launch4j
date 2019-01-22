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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
/**
 * Test case to check that the variables are settable.
 */
class Issue77Test extends FunctionalSpecification {


    def 'Check that dontWrapJar is overwritten'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'notWrapped.exe'
                dontWrapJar = true
                jar = "lib/${projectDir.name}.jar"
            }
            
            
            task usingOverriddenLaunch4j(type: edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
                dontWrapJar = false
                outfile = 'wrapped.exe'
                jar = "lib/${projectDir.name}.jar"
            }

        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World!");
                }
            }
        """

        when:
        def result = build('createExe', 'usingOverriddenLaunch4j')

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':usingOverriddenLaunch4j').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/wrapped.exe')
        def jarfile = new File(projectDir, "build/launch4j/lib/${projectDir.name}.jar")
        then:
        outfile.exists()
        jarfile.exists()
        jarfile.delete()
        !jarfile.exists()


        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == 'Hello World!'
    }

    def 'Check that the default createExe is not wrapped'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'notWrapped.exe'
                dontWrapJar = true
                jar = "lib/${projectDir.name}.jar"
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World!");
                }
            }
        """

        when:
        def result = build( 'createExe')

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/notWrapped.exe')
        def jarfile = new File(projectDir, "build/launch4j/lib/${projectDir.name}.jar")
        then:
        outfile.exists()
        jarfile.exists()


        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == 'Hello World!'

        when:
        jarfile.delete()
        !jarfile.exists()
        def processFailure = outfile.path.execute()
        then:
        processFailure.waitFor() == 0
        processFailure.in.text.trim() != 'Hello World!'
        processFailure.err.text.trim().contains 'com.test.app.Main'
    }
}
