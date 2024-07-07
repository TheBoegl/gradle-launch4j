## Version [3.0.6](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.6)
- **IMPROVEMENT** [#169](https://github.com/TheBoegl/gradle-launch4j/issues/169): Add jar task setter for task provider.
- **IMPROVEMENT** [#168](https://github.com/TheBoegl/gradle-launch4j/issues/168): Improve error handling on launch4j exception.

## Version [3.0.5](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.5)
- **FIX** [#163](https://github.com/TheBoegl/gradle-launch4j/issues/163): Correctly set the JRE not found error message.

- ## Version [3.0.4](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.4)
- **FIX** [#154](https://github.com/TheBoegl/gradle-launch4j/issues/154): Add wrapper manifest support.
- **FIX** [#153](https://github.com/TheBoegl/gradle-launch4j/issues/153): Simplify keeping the libraries next to the executable.

## Version [3.0.3](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.3)
- **FIXUP** The previous release contained an undetected refactoring bug.

## Version [3.0.2](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.2)
- **FIX** [#152](https://github.com/TheBoegl/gradle-launch4j/issues/152): Apply the plugin without the need for the jar plugin.
- **FIX** [#142](https://github.com/TheBoegl/gradle-launch4j/issues/142): Added support for Gradle configuration caching.

## Version [3.0.1](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.1)
- **FIX** [#151](https://github.com/TheBoegl/gradle-launch4j/issues/151): Correctly set the classpath in the created executable.
- **FIX** [#150](https://github.com/TheBoegl/gradle-launch4j/issues/150): Provide setter for all set properties.

## Version [3.0.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v3.0.0)
- **FIX** [#88](https://github.com/TheBoegl/gradle-launch4j/issues/88): Correctly set the minimum required Java version in the created executable and follow the readme specification.
- **FIX** [#144](https://github.com/TheBoegl/gradle-launch4j/issues/144): Update to Launch4j version 3.50.
- **IMPROVEMENT**: Use Gradle properties and providers to improve up-to-date checks.
- **REMOVED**: The deprecated launch4j task and properties as defined in the [Readme](https://github.com/TheBoegl/gradle-launch4j#how-to-configure).

## Version [2.5.4](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.5.4)
- **FIX** [#117](https://github.com/TheBoegl/gradle-launch4j/issues/117): Set default duplicate handling strategy and make property configurable. ([Felix Schnabel](https://github.com/Shadow-Devil))

## Version [2.5.3](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.5.3)
- **FIX** [#118](https://github.com/TheBoegl/gradle-launch4j/issues/118): Version 2.5.2 updated to gradle 7 that ships with groovy 3. The released version was not compatible with gradle 6.

## Version [2.5.2](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.5.2)
- **FIX** [#115](https://github.com/TheBoegl/gradle-launch4j/pull/115): Correct documentation for legacy usage. ([Norbert Wagner](https://github.com/streuspeicher))
- **FIX**: use MavenCentral as jCenter shut down
- **UPDATE**: update plugins and libraries and use latest xstream that fixes [CVE-2021-43859](https://www.opencve.io/cve/CVE-2021-43859).
- **UPDATE**: use latest gradle release (7.3.3)

## Version [2.5.1](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.5.1)
- **FIX** [#109](https://github.com/TheBoegl/gradle-launch4j/issues/109): Version 2.5.0 introduced a backwards incompatible change as the jar property was ignored. Fixed in PR [#111](https://github.com/TheBoegl/gradle-launch4j/pull/111) ([naftalmm](https://github.com/naftalmm))

## Version [2.5.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.5.0)
- **UPDATE** [#103](https://github.com/TheBoegl/gradle-launch4j/issues/103): Update launch4j to version _3.14_. ([Ronaldo Spido](https://github.com/rspido))

## Version [2.4.9](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.9)
- **ISSUE** [#100](https://github.com/TheBoegl/gradle-launch4j/issues/100): Improve logging while creating debug config.xml 

## Version [2.4.8](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.8)
- **FIX** [#96](https://github.com/TheBoegl/gradle-launch4j/pull/96): look for `runtimeClasspath` configuration instead of deprecated `runtime` configuration ([naftalmm](https://github.com/naftalmm)) 
- **INTERNAL** [#95](https://github.com/TheBoegl/gradle-launch4j/pull/95): update dependency to fix deprecation warnings ([naftalmm](https://github.com/naftalmm))

## Version [2.4.7](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.7)
- **FIX** [#68](https://github.com/TheBoegl/gradle-launch4j/issues/68) and [#94](https://github.com/TheBoegl/gradle-launch4j/pull/94): Improve plugin configuration with `copyConfigurable` to avoid the creation of the `libraryDir` folder ([naftalmm](https://github.com/naftalmm))
- **IMPROVEMENT**: Allow convenient access to `copyConfigurable` from Kotlin ([naftalmm](https://github.com/naftalmm))

## Version [2.4.6](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.6)
- **FIX** [#78](https://github.com/TheBoegl/gradle-launch4j/issues/78): Allow to use implementation configuration
- **FIX** [#83](https://github.com/TheBoegl/gradle-launch4j/issues/83): Allow to set only set bundledJrePath.
- **ISSUE** [#82](https://github.com/TheBoegl/gradle-launch4j/issues/82): Use newest xsteam library.

## Version [2.4.5](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.5)
- **FIX** [#77](https://github.com/TheBoegl/gradle-launch4j/issues/77): Do not initialize boolean properties to simplify setting it to another value than the default one.
- **FIX** [#79](https://github.com/TheBoegl/gradle-launch4j/issues/79): Allow to set chdir to an empty value.

## Version [2.4.4](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.4)
- **FIX** [#72](https://github.com/TheBoegl/gradle-launch4j/issues/72): Update the launch4j dependency to version _3.12_ which correctly validates JRE 9 and 10 version numbers.

## Version [2.4.3](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.3)
- **FIX** [#66](https://github.com/TheBoegl/gradle-launch4j/issues/66): Update the xstream dependency to version _1.4.10_.
- **FIX** [#69](https://github.com/TheBoegl/gradle-launch4j/issues/69), [#68](https://github.com/TheBoegl/gradle-launch4j/issues/68) and, [#51](https://github.com/TheBoegl/gradle-launch4j/issues/51): Improve documentation.

## Version [2.4.2](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.2)
- **FIX** [#62](https://github.com/TheBoegl/gradle-launch4j/issues/62): Correctly read `restartOnCrash` from the configuration. ([Jacob Ilsø Christensen](https://github.com/jacobilsoe))
- *INTERNAL* [#61](https://github.com/TheBoegl/gradle-launch4j/issues/61): Ignore gradle task name cache. ([Oliver](https://github.com/obearn))

## Version [2.4.1](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.1)
- **FIX** [#60](https://github.com/TheBoegl/gradle-launch4j/issues/60): Add work around for [gradle/#2650](https://github.com/gradle/gradle/issues/2650)
## Version [2.4.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.4.0)
- **NEW** [#48](https://github.com/TheBoegl/gradle-launch4j/issues/48): Add last resort fallback option classpath `Set<String> classpath`.
- **FIX** [#52](https://github.com/TheBoegl/gradle-launch4j/issues/52): Use launch4j version _3.11_ which added the linux 64 bit binaries.
- **FIX** [#53](https://github.com/TheBoegl/gradle-launch4j/issues/53): Update the xstream dependency to version _1.4.9_.
- **FIX** [#54](https://github.com/TheBoegl/gradle-launch4j/issues/54): Add test case for the gradle versions _3.3_ and _3.4.1_.
- **FIX** [#56](https://github.com/TheBoegl/gradle-launch4j/issues/56): Correct the table rendering in the README's description ([thc202](https://github.com/thc202))
- **FIX** [#58](https://github.com/TheBoegl/gradle-launch4j/issues/58): Correct the example dependency ([Colin Rudd](https://github.com/cnrudd))
- **NEW** [#50](https://github.com/TheBoegl/gradle-launch4j/issues/59): Add the `variables` property to set the environment variables.

## Version [2.3.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.3.0)
- **FIX** [#45](https://github.com/TheBoegl/gradle-launch4j/issues/45): Add the missing message if an instance already exists.
- **FIX** [#46](https://github.com/TheBoegl/gradle-launch4j/issues/46): Migrate the jvm options from `String opt` to `Set<String> jvmOptions`.
- **FIX** [#47](https://github.com/TheBoegl/gradle-launch4j/issues/47): Only depend on one of the jar generating tasks.
- **FIX** [#48](https://github.com/TheBoegl/gradle-launch4j/issues/48): Allow a customized classpath modification.
- **FIX** [#49](https://github.com/TheBoegl/gradle-launch4j/issues/49): Keep a copy of the generated launch4j xml with the `l4j-debug` project property.

## Version [2.2.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.2.0)
- **FIX** [#43](https://github.com/TheBoegl/gradle-launch4j/issues/43): Relativize absolute icon and splash paths to the outfile.

## Version [2.1.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.1.0)
- **FIX** [#35](https://github.com/TheBoegl/gradle-launch4j/issues/35): Avoid jar version mismatch in launch4jäs lib folder.
- **FIX** [#37](https://github.com/TheBoegl/gradle-launch4j/issues/37): Allow manual control of the classpath.

## Version [2.0.1](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.0.1)
- **FIX** [#34](https://github.com/TheBoegl/gradle-launch4j/issues/34): Fix compatibility issues with Gradle version 2.

## Version [2.0.0](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v2.0.0)
Rework and restructure the plugin
- **NEW**: Add customizable task types `edu.sc.seis.launch4j.tasks.Launch4jLibraryTask` and `edu.sc.seis.launch4j.tasks.Launch4jExternalTask`
- **FIX** [#7](https://github.com/TheBoegl/gradle-launch4j/issues/7): Create multiple executables with one gradle run.
- **FIX** [#24](https://github.com/TheBoegl/gradle-launch4j/issues/24): Exposed tasks are not replaced.
- **FIX** [#29](https://github.com/TheBoegl/gradle-launch4j/issues/29): Clean tasks work correctly new (see [#33](https://github.com/TheBoegl/gradle-launch4j/issues/33))
- **FIX** [#33](https://github.com/TheBoegl/gradle-launch4j/issues/33): Tasks expose correct outputs.
- **DEPRECATED**: The property `description` is deprecated in favor of the property `fileDescription` to avoid a name conflict with the task's description.
- **DEPRECATED**: The task `launch4j` is deprecated in favor of `createExe` task to avoid a name conflict between this placeholder task and the launch4j configuration.
- **REMOVED**: The tasks `generateXmlConfig`, `copyL4jLib`, `copyL4jBinLib`, `unzipL4jBin` `createExeWithBin`, and `createExeWithJar` are implemented internally instead of tasks.
The *createExeWithJar* task's functionality is implemented in the *createExe* task. 

- **REMOVED**: The properties `launch4jCmd` and `externalLaunch4j` 


## Version [1.6.2](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v1.6.2)
- **FIX** [#30](https://github.com/TheBoegl/gradle-launch4j/issues/30): update to launch4j Version 3.9  
Add these properties:
  - `trademarks`
  - `language`

## Version [1.6.1](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v1.6.1)
- **NEW**: Add option `libraryDirLaunch4j`
- **CHANGE**: The property `outfile` may be a relative path, which will be created and the library files will be relativized against this path.

## Version [1.6](https://github.com/TheBoegl/gradle-launch4j/releases/tag/v1.6)
- **NEW**: Add property `libraryDir` to change the classpath folder for the dependencies 
([FFourtyTwo](https://github.com/FFourtyTwo))
