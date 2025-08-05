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

package edu.sc.seis.launch4j.util;

import java.util.concurrent.TimeUnit;

final class ProcessHelper {

    static Process executeWithEnvironment(File executable) {
        def builder = new ProcessBuilder(executable.path)
        builder.environment().put("JAVA_HOME", System.getProperty("java.home"))
        builder.start()
    }

    static int waitFor(Process process) throws InterruptedException {
        waitFor(process, 8)
    }

    static int waitFor(Process process, int seconds) throws InterruptedException {
        if (!process.waitFor(seconds, TimeUnit.SECONDS)) {
            process.destroyForcibly()
        }
        process.exitValue()
    }

}
