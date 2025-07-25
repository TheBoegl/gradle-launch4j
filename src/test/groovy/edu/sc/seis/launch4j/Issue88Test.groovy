/*
 * Copyright (c) 2025 Sebastian Boegl
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
import edu.sc.seis.launch4j.util.ProcessHelper

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Issue88Test extends FunctionalSpecification {
    def 'verify source compatibility is used as minimum version'() {
        given:
        buildFile << """
            sourceCompatibility = 1.7
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>1.7.0</minVersion>')

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == '...'
    }

    def 'verify toolchain is used as minimum version'() {
        given:
        buildFile << """
            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(8) // use same version as the one building and provided below
                }
            }
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        // disable auto-detect and put the current jdk on the search path as this fails otherwise
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug', '-Porg.gradle.java.installations.auto-detect=false', '-Porg.gradle.java.installations.paths=' + System.getProperty("java.home")).build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>1.8.0</minVersion>')

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == '...'
    }

    def 'verify source compatibility in java plugin is used as minimum version'() {
        given:
        // we cannot use a newer version like JavaVersion.VERSION_17
        // as we cannot compile it and the javaCompile tasks fails
        buildFile << """
            java {
                sourceCompatibility = JavaVersion.VERSION_1_7
            }
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains('<minVersion>1.7.0</minVersion>')

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()
    }

    def 'verify minimum version works as expected'() {
        given:
        buildFile << """
            java.sourceCompatibility = JavaVersion.VERSION_1_8
            launch4j {
                outfile = 'test.exe'
                jreMinVersion = '1.8.0_281'
                bundledJrePath = 'jre'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains("<minVersion>1.8.0_281</minVersion>")

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = outfile.path.execute()
        then:
        process.waitFor() == 0
        process.in.text.trim() == '...'
    }

    def 'verify minimum version defaults to current java version'() {
        given:
        buildFile << """
            launch4j {
                outfile = 'test.exe'
                bundledJrePath = '%JAVA_HOME%'
            }
        """

        when:
        def result = createAndConfigureGradleRunner('createExe', '-Pl4j-debug').build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':createExe').outcome == SUCCESS

        when:
        def xmlFile = new File(projectDir, 'build/tmp/createExe/createExe.xml')
        then:
        xmlFile.exists()

        when:
        def xml = xmlFile.text
        then:
        xml.contains("<minVersion>${getExpectedJavaVersion()}</minVersion>")

        when:
        def outfile = new File(projectDir, 'build/launch4j/test.exe')
        then:
        outfile.exists()

        when:
        def process = ProcessHelper.executeWithEnvironment(outfile)
        then:
        ProcessHelper.waitFor(process) == 0
        process.in.text.trim() == '...'
    }

}
