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

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

@CompileStatic
interface Launch4jConfiguration {

    String getOutputDir()

    File getOutputDirectory()

    String getOutfile()

    @OutputFile
    File getDest()

    @Internal("usually the xml file is deleted anyways")
    File getXmlFile()

    String getLibraryDir()

    String getMainClassName()

    String getJar()

    Boolean getDontWrapJar()

    String getHeaderType()

    String getErrTitle()

    String getCmdLine()

    String getChdir()

    String getPriority()

    String getDownloadUrl()

    String getSupportUrl()

    Boolean getStayAlive()

    Boolean getRestartOnCrash()

    String getManifest()

    String getIcon()

    String getVersion()

    String getTextVersion()

    String getCopyright()

    Set<String> getJvmOptions()

    /**
     * this is a backwards compatible setter for opts which was a String.
     * @param opt the JVM options
     * @deprecated since 2.3 use {@link #getJvmOptions()}
     */
    @Deprecated
    void setOpt(String opt)

    String getCompanyName()

    String getFileDescription()

    String getProductName()

    String getInternalName()

    String getTrademarks()

    String getLanguage()

    String getBundledJrePath()

    Boolean getBundledJre64Bit()

    Boolean getBundledJreAsFallback()

    String getJreMinVersion()

    String internalJreMinVersion()

    String getJreMaxVersion()

    String getJdkPreference()

    String getJreRuntimeBits()

    Set<String> getVariables()

    String getMutexName()

    String getWindowTitle()

    String getMessagesStartupError()

    String getMessagesBundledJreError()

    String getMessagesJreVersionError()

    String getMessagesLauncherError()

    String getMessagesInstanceAlreadyExists()

    Integer getInitialHeapSize()

    Integer getInitialHeapPercent()

    Integer getMaxHeapSize()

    Integer getMaxHeapPercent()

    String getSplashFileName()

    Boolean getSplashWaitForWindows()

    Integer getSplashTimeout()

    Boolean getSplashTimeoutError()

    Set<String> getClasspath()
}
