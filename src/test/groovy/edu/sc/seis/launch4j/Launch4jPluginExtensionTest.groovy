package edu.sc.seis.launch4j

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class Launch4jPluginExtensionTest extends Specification {
    private static final DEBUG = Boolean.parseBoolean(System.getProperty("org.gradle.testkit.debug", "false"))
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    List<File> pluginClasspath

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        def pluginClasspathResource = getClass().classLoader.findResource('plugin-classpath.txt')
        if (pluginClasspathResource == null) {
            throw new IllegalStateException('Plugin classpath resource file not found. Run the "testClasses" task.')
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    }

    def 'Applying the plugin provides properties'() {
        given:
        buildFile << """
            plugins {
                id 'edu.sc.seis.launch4j'
            }

            task printProperties << {
                println launch4j.outputDir
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'printProperties')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':printProperties').outcome == SUCCESS
        result.output.trim().equals('launch4j')
    }

    def 'Running the task to create the executable succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
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
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS
    }

    def 'Running the library task to create the executable succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'Launch4j.exe'
            }

            task libraryTask(type: edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
                outfile = 'Test1234.exe'
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
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'createExe', 'libraryTask')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS // jar task has to be called
        result.task(':createExe').outcome == SUCCESS
        result.task(':libraryTask').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, "build/launch4j/Launch4j.exe")
        outfile.exists()
        def outFileLibraryTask = new File(testProjectDir.root, 'build/launch4j/Test1234.exe')
        outFileLibraryTask.exists()
    }

    def 'Running the library task with a different output directory'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'Launch4j.exe'
                outputDir = 'launch4j2'
            }

            task libraryTask(type: edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
                outfile = 'Test1234.exe'
                outputDir = 'launch4j-test'
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
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'createExe', 'libraryTask')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS // jar task has to be called
        result.task(':createExe').outcome == SUCCESS
        result.task(':libraryTask').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, "build/launch4j2/Launch4j.exe")
        outfile.exists()
        def outFileLibraryTask = new File(testProjectDir.root, 'build/launch4j-test/Test1234.exe')
        outFileLibraryTask.exists()
    }

    def 'Running the library task to create the executable with more options succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
            }

            task libraryTask(type: edu.sc.seis.launch4j.tasks.Launch4jLibraryTask) {
                outfile = 'Test1234.exe'
                xmlFileName = 'Test1234.xml'
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
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'libraryTask')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':libraryTask').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/Test1234.exe')
        outfile.exists()
    }

    def 'Running the created executable succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

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
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello World!')
    }

    def 'Running the created executable with Java dependencies succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'test.exe'
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }

    def 'Running the created executable in a subfolder with Java dependencies succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'app/bin/test.exe'
                libraryDir = 'app/lib'
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/app/bin/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }

    def 'Running an unwrapped executable with Java dependencies succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
            }

            ext {
                mainTestClassName = 'com.test.app.Main'
            }

            jar {
                manifest {
                    attributes 'Main-Class': mainTestClassName
                }
            }

            launch4j {
                mainClassName = mainTestClassName
                outfile = 'test.exe'
                dontWrapJar = true
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }

    def 'Running an unwrapped executable jar as executable with Java dependencies succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
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
                dontWrapJar = true
            }
        """

        File sourceFile = new File(testProjectDir.newFolder('src', 'main', 'java'), 'Main.java')
        sourceFile << """
            package com.test.app;

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }

    def 'Running a fat executable jar as executable with Java dependencies succeeds'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
                id 'com.github.johnrengelman.shadow' version '1.2.4'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
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

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }

    def 'Running a fat executable shadowJar as executable with Java dependencies succeeds with shadowJar task'() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'edu.sc.seis.launch4j'
                id 'com.github.johnrengelman.shadow' version '1.2.4'
            }

            repositories {
                mavenCentral()
            }

            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
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

            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;

            public class Main {
                private static final Logger LOG = LoggerFactory.getLogger(Main.class);

                public static void main(String[] args) {
                    System.out.println("Hello STDOUT!");
                    LOG.info("Hello LOG!");
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(DEBUG)
                .withProjectDir(testProjectDir.root)
                .withArguments('-q', 'jar', 'shadowJar', 'launch4j')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':shadowJar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/test.exe')
        outfile.exists()

        def process = outfile.path.execute()
        process.waitFor() == 0
        process.in.text.trim().equals('Hello STDOUT!')
        process.err.text.trim().endsWith('Hello LOG!')
    }
}
