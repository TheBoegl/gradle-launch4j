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
class Issue59Test extends FunctionalSpecification {


    def 'Check that the variables are set and retrieved correctly'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                variables = ['a=21', 'b=42']
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;
            
            import java.util.Locale;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("a is " + System.getenv("a") + ", b is " + System.getenv("b") + " and c is " + System.getenv("c"));
                }
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
        process.in.text.trim() == 'a is 21, b is 42 and c is null'
    }
}
