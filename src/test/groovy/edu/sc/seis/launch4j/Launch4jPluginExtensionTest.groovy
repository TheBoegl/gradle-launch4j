package edu.sc.seis.launch4j

import edu.sc.seis.launch4j.util.FunctionalSpecification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class Launch4jPluginExtensionTest extends FunctionalSpecification {

    def 'Applying the plugin provides properties'() {
        given:
        buildFile << """
            task printProperties {
                doLast {
                    println launch4j.outputDir
                }
            }
        """

        when:
        def result = build('-q', 'printProperties')

        then:
        result.task(':printProperties').outcome == SUCCESS
        result.output.trim().equals('launch4j')
    }

    def 'Running the task to create the executable succeeds'() {
        given:
        buildFile << """
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
        def result = build('jar', 'launch4j')

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':launch4j').outcome == SUCCESS
    }

    def 'Running the library task to create the executable succeeds'() {
        given:
        buildFile << """
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
        def result = build('jar', 'createExe', 'libraryTask')

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
        def result = build('jar', 'createExe', 'libraryTask')

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
        def result = build('jar', 'libraryTask')

        then:
        result.task(':jar').outcome == SUCCESS
        result.task(':libraryTask').outcome == SUCCESS

        def outfile = new File(testProjectDir.root, 'build/launch4j/Test1234.exe')
        outfile.exists()
    }

    def 'Running the created executable succeeds'() {
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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'launch4j')

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

    def 'Running the created executable in a subfolder and default lib folder with Java dependencies succeeds'() {
        given:
        buildFile << """
            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'app/bin/test.exe'
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
        def result = build('jar', 'launch4j')

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

    def 'Running the created executable in a subfolder and changed lib folder with Java dependencies succeeds'() {
        given:
        buildFile << """
            dependencies {
                compile 'org.slf4j:slf4j-api:1.7.12'
                runtime 'org.slf4j:slf4j-simple:1.7.12'
            }

            launch4j {
                mainClassName = 'com.test.app.Main'
                outfile = 'app/bin/test.exe'
                dontWrapJar = true
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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'launch4j')

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
        def result = build('jar', 'shadowJar', 'launch4j')

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
