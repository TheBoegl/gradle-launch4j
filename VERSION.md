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
([FFourtyTwo](//github.com/FFourtyTwo))
