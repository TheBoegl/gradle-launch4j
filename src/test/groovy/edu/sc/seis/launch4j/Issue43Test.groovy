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
import org.gradle.internal.impldep.com.google.common.io.Files

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue43Test extends FunctionalSpecification {

    @Override
    def setup() {
        File icon = new File(FunctionalSpecification.class.classLoader.findResource('main.ico').toURI())
        Files.copy(icon, new File(projectDir, 'main.ico'))

        File splash = new File(FunctionalSpecification.class.classLoader.findResource('splash.bmp').toURI())
        Files.copy(splash, new File(projectDir, 'splash.bmp'))
    }

    def 'Running the task to create the executable with a relative icon and splash path succeeds'() {
        given:

        // the testIcon `${projectDir}/main.ico` is located in the grandparent folder of the launch4j working directory `${buildDir}/launch4j` -> `icon = "../../main.ico"`
        buildFile << '''
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                icon = "../../main.ico"
                splashFileName = '../../splash.bmp'
            }
        '''

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
        process.in.text.trim() == 'Hello World!'

    }

    def 'Running the task to create the executable with an absolute icon and splash path succeeds'() {
        given:

        File testIcon = new File(projectDir, 'main.ico')
        File icon = new File(FunctionalSpecification.class.classLoader.findResource('main.ico').toURI())
        Files.copy(icon, testIcon)

        buildFile << '''
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                icon = "${projectDir}/main.ico"
                splashFileName = "${projectDir}/splash.bmp"
            }
        '''

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
        process.in.text.trim() == 'Hello World!'
    }

    def 'Running the task to create the executable without an icon or splash file succeeds'() {
        given:

        File testIcon = new File(projectDir, 'main.ico')
        File icon = new File(FunctionalSpecification.class.classLoader.findResource('main.ico').toURI())
        Files.copy(icon, testIcon)

        buildFile << '''
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
            }
        '''

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
        process.in.text.trim() == 'Hello World!'
    }
}
