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

class Issue37Test extends FunctionalSpecification {

    def 'Selecting only a few libraries out of a multi project build succeeds'() {
        given:
        buildFile << """
project(':test1') {
    apply plugin: 'java'
    apply plugin: 'application'
    mainClassName = 'com.test.app.Main'
}
project(':test2') {
    apply plugin: 'java'
    apply plugin: 'application'
    mainClassName = 'com.test.app.Main'
}
project(':test3') {
    apply plugin: 'java'
    apply plugin: 'application'
    mainClassName = 'com.test.app.Main'
    
    dependencies { 
        compile project(':test2')
    }
}

dependencies {
    compile project(':test1')
    compile project(':test3')
}
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'Test.exe'
                copyConfigurable = project.jar.outputs.files + project(':test3').jar.outputs.files
            }
        """
        testProjectDir.newFile('settings.gradle') << """
            rootProject.name = 'testProject'
            include 'test1'
            include 'test2'
            include 'test3'
        """

        new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("RootProject: Hello World!");
                }
            }
        """
        new File(testProjectDir.newFolder('test1', 'src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Test1: Hello World!");
                }
            }
        """
        new File(testProjectDir.newFolder('test2', 'src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Test2: Hello World!");
                }
            }
        """
        new File(testProjectDir.newFolder('test3', 'src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Test3: Hello World!");
                }
            }
        """

        when:
        def result = build('createExe')

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        new File(projectDir, 'build/launch4j/Test.exe').exists()
        new File(projectDir, 'build/launch4j/lib/testProject.jar').exists()
        !new File(projectDir, 'build/launch4j/lib/test1.jar').exists()
        ! new File(projectDir, 'build/launch4j/lib/test2.jar').exists()
        new File(projectDir, 'build/launch4j/lib/test3.jar').exists()
    }
}
