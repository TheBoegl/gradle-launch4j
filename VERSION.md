## Version [2.2.0](../../releases/tag/v2.2.0)
- **FIX** [#43](../../issues/43): Relativize absolute icon and splash paths to the outfile.
## Version [2.1.0](../../releases/tag/v2.1.0)
- **FIX** [#35](../../issues/35): Avoid jar version mismatch in launch4jäs lib folder.
- **FIX** [#37](../../issues/37): Allow manual control of the classpath.

## Version [2.0.1](../../releases/tag/v2.0.1)
- **FIX** [#34](../../issues/34): Fix compatibility issues with Gradle version 2.

## Version [2.0.0](../../releases/tag/v2.0.0)
Rework and restructure the plugin
- **NEW**: Add customizable task types `edu.sc.seis.launch4j.tasks.Launch4jLibraryTask` and `edu.sc.seis.launch4j.tasks.Launch4jExternalTask`
- **FIX** [#7](../../issues/7): Create multiple executables with one gradle run.
- **FIX** [#24](../../issues/24): Exposed tasks are not replaced.
- **FIX** [#29](../../issues/29): Clean tasks work correctly new (see [#33](../../issues/33))
- **FIX** [#33](../../issues/33): Tasks expose correct outputs.
- **DEPRECATED**: The property `description` is deprecated in favor of the property `fileDescription` to avoid a name conflict with the task's description.
- **DEPRECATED**: The task `launch4j` is deprecated in favor of `createExe` task to avoid a name conflict between this placeholder task and the launch4j configuration.
- **REMOVED**: The tasks `generateXmlConfig`, `copyL4jLib`, `copyL4jBinLib`, `unzipL4jBin` `createExeWithBin`, and `createExeWithJar` are implemented internally instead of tasks.
The *createExeWithJar* task's functionality is implemented in the *createExe* task. 

- **REMOVED**: The properties `launch4jCmd` and `externalLaunch4j` 


## Version [1.6.2](../../releases/tag/v1.6.2)
- **FIX** [#30](../../issues/30): update to launch4j Version 3.9  
Add these properties:
  - `trademarks`
  - `language`

## Version [1.6.1](../../releases/tag/v1.6.1)
- **NEW**: Add option `libraryDirLaunch4j`
- **CHANGE**: The property `outfile` may be a relative path, which will be created and the library files will be relativized against this path.

## Version [1.6](../../releases/tag/v1.6)
- **NEW**: Add property `libraryDir` to change the classpath folder for the dependencies 
([FFourtyTwo](//github.com/FFourtyTwo))
