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
 * Test case to check jar task is not in the task graph if shadowjar is called.
 */
class Issue47Test extends FunctionalSpecification {


    def 'Check that the shadowJar task is not present on a normal build succeeds'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
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
        def result = build('createExe')

        then:
        !result.task(':shadowJar')
        result.task(':jar')
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS
    }

    def 'Check that the jar task is not present on a shadowJar build succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'com.github.johnrengelman.shadow' version '4.0.4'
            }

            repositories {
                jcenter()
            }

            ext {
                mainTestClassName = 'com.test.app.Main'
            }

            jar {
                manifest {
                    attributes 'Main-Class': mainTestClassName
                    attributes 'Class-Path': configurations.runtime.collect { it.getName() }.join(' ')
                }
            }

            launch4j {
                outfile = 'test.exe'
                copyConfigurable = project.tasks.shadowJar.outputs.files
                jar = "lib/" + project.tasks.shadowJar.archiveName
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
        def result = build('createExe')

        then:
        !result.task(':jar')
        result.task(':shadowJar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS
    }
}
