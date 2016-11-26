package edu.sc.seis.launch4j

interface Launch4jConfiguration {

    String getOutputDir()

    File getOutputDirectory()

    String getOutfile()

    File getDest()

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

    String getOpt()

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

    String getMutexName()

    String getWindowTitle()

    String getMessagesStartupError()

    String getMessagesBundledJreError()

    String getMessagesJreVersionError()

    String getMessagesLauncherError()

    Integer getInitialHeapSize()

    Integer getInitialHeapPercent()

    Integer getMaxHeapSize()

    Integer getMaxHeapPercent()

    String getSplashFileName()

    Boolean getSplashWaitForWindows()

    Integer getSplashTimeout()

    Boolean getSplashTimeoutError()
}