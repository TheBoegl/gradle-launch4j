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

class Issue150Test extends FunctionalSpecification {

    def 'Verify that the classpath is set correctly'() {
        given:
        buildFile << """
launch4j {
    mainClassName = 'com.test.app.Main'
    outfile = 'test.exe'
    classpath = ['libA', 'libB']
}
"""

        when:
        def result = build('createExe', '-Pl4j-debug')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xml = new File(projectDir, 'build/tmp/createExe/createExe.xml')

        then:
        xml.exists()

        when:
        def xmlText = xml.text

        then:
        xmlText.contains('<cp>libA</cp>')
        xmlText.contains('<cp>libB</cp>')
    }

    def 'Verify that the jvmOptions are set correctly'() {
        given:
        buildFile << """
launch4j {
    mainClassName = 'com.test.app.Main'
    outfile = 'test.exe'
    jvmOptions = ['optionA', 'optionB']
}
"""

        when:
        def result = build('createExe', '-Pl4j-debug')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xml = new File(projectDir, 'build/tmp/createExe/createExe.xml')

        then:
        xml.exists()

        when:
        def xmlText = xml.text

        then:
        xmlText.contains('<opt>optionA</opt>')
        xmlText.contains('<opt>optionB</opt>')
    }

    def 'Verify that the variables are set correctly'() {
        given:
        buildFile << """
launch4j {
    mainClassName = 'com.test.app.Main'
    outfile = 'test.exe'
    variables = ['varA=A', 'varB=B']
}
"""

        when:
        def result = build('createExe', '-Pl4j-debug')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xml = new File(projectDir, 'build/tmp/createExe/createExe.xml')

        then:
        xml.exists()

        when:
        def xmlText = xml.text

        then:
        xmlText.contains('<var>varA=A</var>')
        xmlText.contains('<var>varB=B</var>')
    }

    def 'Verify that custom tasks can be registered'() {
        given:
        testProjectDir.newFile("extension.gradle") << """
buildscript {
    repositories{
        gradlePluginPortal()
    }
    dependencies {
        classpath 'edu.sc.seis.launch4j:launch4j:3.0.0'
    }
}

tasks.register('createFastStart', edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
    outfile = 'fast.exe'
}
"""
        buildFile << """
launch4j {
    mainClassName = 'com.test.app.Main'
    outfile = 'test.exe'
}

// apply from: 'extension.gradle' would work for real but not in the tests
tasks.register('createFastStart', edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
    outfile = 'fast.exe'
}
"""

        when:
        def result = build('createAllExecutables')
        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS
        result.task(':createFastStart').outcome == SUCCESS

        when:
        def testExe = new File(projectDir, 'build/launch4j/test.exe')
        then:
        testExe.exists()

        when:
        def fastExe = new File(projectDir, 'build/launch4j/fast.exe')
        then:
        fastExe.exists()
    }

}
