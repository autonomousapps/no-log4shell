See also:
https://blog.gradle.org/log4j-vulnerability

## Build scripts

In your projects, apply with `plugins` syntax:
```
plugins {
  id 'com.autonomousapps.no-log4shell' version '<<latest version>>'
}
```

Legacy apply:
```
buildscript {
  repositories {
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    classpath "com.autonomousapps:no-log4shell:0.1"
  }
}

apply plugin: "com.autonomousapps.no-log4shell"
```

## Settings scripts

In your `settings.gradle[.kts]`, apply with `plugins` syntax:
```
plugins {
  id 'com.autonomousapps.no-log4shell-settings' version '<<latest version>>'
}
```

Legacy apply:
```
buildscript {
  dependencies {
    classpath "com.autonomousapps:no-log4shell:0.3"
  }
}

apply plugin: "com.autonomousapps.no-log4shell-settings"
```