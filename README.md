See also:
https://blog.gradle.org/log4j-vulnerability

Apply with `plugins` syntax:
```
plugins {
  id 'com.autonomousapps.no-log4shell' version '<<latest version>>'
}
```

Legacy apply:
```
buildscript {
  dependencies {
    classpath 'com.autonomousapps.no-log4shell.gradle.plugin:<<latest version>>'
  }
}
apply plugin: 'com.autonomousapps.no-log4shell'
```
