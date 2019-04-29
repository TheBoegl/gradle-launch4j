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

class Issue83Test extends FunctionalSpecification {

    def 'setting only the path is possible'() {
        given:
        def jrePath = System.getProperties().getProperty("java.home").replace("\\", "/")
        buildFile << """
            launch4j {
                bundledJrePath = '$jrePath'
                mainClassName = 'com.test.app.Main'
                outfile = 'Test.exe'
            }
        """
        new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java') << """
            package com.test.app;

            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World!");
                }
            }
        """
        testProjectDir.newFile('settings.gradle').text = "rootProject.name = 'testProject'"

        when:
        def result = build('createExe', '-Pl4j-debug') // use debug flag to export xml

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def outfile = new File(projectDir, 'build/launch4j/Test.exe')
        def xml = new File(projectDir, 'build/tmp/createExe/createExe.xml')


        then:
        outfile.exists()
        xml.exists()
        def xmlText = xml.text
        xmlText.contains("<path>$jrePath</path>")
        xmlText.contains('<minVersion></minVersion')
        xmlText.contains('<maxVersion></maxVersion')

        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
    }
}
