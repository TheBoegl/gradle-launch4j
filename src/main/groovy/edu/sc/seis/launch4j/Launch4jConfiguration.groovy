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

import groovy.transform.CompileStatic
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*

import java.nio.file.Path

@CompileStatic
interface Launch4jConfiguration {
    @Input
    @Optional
    Property<String> getOutputDir()

    @OutputDirectory
    DirectoryProperty getOutputDirectory()

    @OutputFile
    Provider<RegularFile> getDest()

    @Internal("usually the xml file is deleted anyways")
    Provider<RegularFile> getXmlFile()
    @Internal
    Provider<RegularFile> getLibraryDirectory()

    Property<String> getMainClassName()

    Property<FileCollection> getJarFiles()

    void setJarTask(Task task)

    Path getJarTaskOutputPath()

    Path getJarTaskDefaultOutputPath()
    Property<String> getLibraryDir()
    Property<String> getXmlFileName()
    Property<String> getOutfile()

    Property<Boolean> getDontWrapJar()

    Property<String> getHeaderType()

    Property<String> getErrTitle()

    Property<String> getCmdLine()

    Property<String> getChdir()

    Property<String> getPriority()

    Property<String> getDownloadUrl()

    Property<String> getSupportUrl()

    Property<Boolean> getStayAlive()

    Property<DuplicatesStrategy> getDuplicatesStrategy()

    Property<Boolean> getRestartOnCrash()

    Property<String> getIcon()

    Property<String> getVersion()

    Property<String> getTextVersion()

    Property<String> getCopyright()

    SetProperty<String> getJvmOptions()

    Property<String> getCompanyName()

    Property<String> getFileDescription()

    Property<String> getProductName()

    Property<String> getInternalName()

    Property<String> getTrademarks()

    Property<String> getLanguage()

    Property<String> getBundledJrePath()

    Property<Boolean> getRequires64Bit()

    Property<String> getJreMinVersion()

    String internalJreMinVersion()

    Property<String> getJreMaxVersion()

    Property<Boolean> getRequiresJdk()

    SetProperty<String> getVariables()

    Property<String> getMutexName()

    Property<String> getWindowTitle()

    Property<String> getMessagesStartupError()

    Property<String> getMessagesJreNotFoundError()

    Property<String> getMessagesJreVersionError()

    Property<String> getMessagesLauncherError()

    Property<String> getMessagesInstanceAlreadyExists()

    Property<Integer> getInitialHeapSize()

    Property<Integer> getInitialHeapPercent()

    Property<Integer> getMaxHeapSize()

    Property<Integer> getMaxHeapPercent()

    Property<String> getSplashFileName()

    Property<Boolean> getSplashWaitForWindows()

    Property<Integer> getSplashTimeout()

    Property<Boolean> getSplashTimeoutError()

    SetProperty<String> getClasspath()
}
