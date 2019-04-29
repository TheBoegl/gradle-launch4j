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
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
/**
 * Test case to check the implementation and the backwards compatibility of the change from <tt>String opt</tt> to <tt>String[] opt</tt>.
 */
class Issue46Test extends FunctionalSpecification {

    @Unroll
    def 'Running the task to create the executable with the deprecated opt for language #language succeeds'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                opt = "-Duser.language=${language}"
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;
            
            import java.util.Locale;

            public class Main {
                public static void main(String[] args) {
                    if ("${language}".equals(Locale.getDefault().getLanguage())) {
                        System.out.println("${message}");
                    } else {
                        System.out.println("wrong language!");
                    }
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
        process.in.text.trim() == message

        where:
        language    | message
        'de'        | 'Hallo Welt!'
        'en'        | 'Hello World!'
//        'fr'        | 'Bonjour monde!'
//        'it'        | 'Ciao mondo!'
    }


    @Unroll
    def 'Running the task to create the executable with jvmOptions for language #language succeeds'() {
        given:
        buildFile << """
            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
                jvmOptions = [ "-Duser.language=${language}" ]
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;
            
            import java.util.Locale;

            public class Main {
                public static void main(String[] args) {
                    if ("${language}".equals(Locale.getDefault().getLanguage())) {
                        System.out.println("${message}");
                    } else {
                        System.out.println("wrong language!");
                    }
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
        process.in.text.trim() == message

        where:
        language    | message
        'de'        | 'Hallo Welt!'
        'en'        | 'Hello World!'
//        'fr'        | 'Bonjour monde!'
//        'it'        | 'Ciao mondo!'
    }

    def 'Running the task to create the executable without jvm options succeeds'() {
        given:

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
