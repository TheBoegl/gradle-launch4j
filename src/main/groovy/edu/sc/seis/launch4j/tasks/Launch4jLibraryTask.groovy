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

package edu.sc.seis.launch4j.tasks

import edu.sc.seis.launch4j.Extract
import edu.sc.seis.launch4j.Launch4jPlugin
import groovy.transform.CompileStatic
import net.sf.launch4j.Builder
import net.sf.launch4j.Log
import net.sf.launch4j.config.ConfigPersister
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GradleVersion

class Launch4jLibraryTask extends DefaultLaunch4jTask {

    @TaskAction
    def run() {
        Extract.binaries(project)
        createExecutableFolder()
        if (GradleVersion.current() < GradleVersion.version('3.0')) {
            System.setProperty('javax.xml.parsers.DocumentBuilderFactory', 'com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl')
            System.setProperty('javax.xml.parsers.SAXParserFactory', 'com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl')
        }
        createXML(copyLibraries())
        File xml = getXmlFile()
        ConfigPersister.getInstance().load(xml)
        Builder b = new Builder(new GradleLogger(project.logger), new File(project.buildDir, Launch4jPlugin.LAUNCH4J_BINARY_DIRECTORY))
        b.build()
        if (project.hasProperty("l4j-debug")) {
            new File(temporaryDir, xml.name).text = xml.text
        }
        project.delete(xml)
    }

    @CompileStatic
    private static class GradleLogger extends Log {

        private Logger logger

        GradleLogger(Logger logger) {
            this.logger = logger
        }

        @Override
        void clear() {
            // nothing to do here
        }

        @Override
        void append(String line) {
            logger.info("Launch4j", line)
        }
    }

}
