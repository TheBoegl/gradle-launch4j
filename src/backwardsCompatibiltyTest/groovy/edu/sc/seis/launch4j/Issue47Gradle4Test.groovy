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
class Issue47Gradle4Test extends FunctionalSpecification {


    def 'Check that the shadowJar task is not present on a fatJar build succeeds'() {
        given:
        buildFile << """
            plugins {
                id "eu.appsatori.fatjar" version "0.3"
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

            fatJar {
                classifier 'fat'
                manifest {
                    attributes 'Main-Class': mainTestClassName
                }
            }
            
            fatJarPrepareFiles.dependsOn jar
            
            launch4j {
                outfile = 'test.exe'
                mainClassName = mainTestClassName
                copyConfigurable = project.tasks.fatJar.outputs.files
                jar = "lib/" + project.tasks.fatJar.archiveName
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
        def result =  createAndConfigureGradleRunner('createExe').withGradleVersion('4.10.2').build()

        then:
        result.task(':jar') // the task is added as fatJar dependency
        !result.task(':shadowJar')
        result.task(':fatJar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS
    }
}
