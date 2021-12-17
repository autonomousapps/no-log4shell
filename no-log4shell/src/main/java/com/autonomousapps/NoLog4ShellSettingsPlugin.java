package com.autonomousapps;

import org.gradle.api.GradleException;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.util.GradleVersion;

@SuppressWarnings("unused")
@NonNullApi
public class NoLog4ShellSettingsPlugin implements Plugin<Settings> {

  @Override public void apply(Settings settings) {
    GradleVersion version = GradleVersion.version(settings.getGradle().getGradleVersion());
    if (version.compareTo(GradleVersion.version("7.3.2")) >= 0) {
      throw new GradleException("This plugin is unnecessary if using Gradle >= 7.3.2.");
    }

    settings.getGradle().rootProject(rootProject -> rootProject
        .getBuildscript()
        .getDependencies()
        .constraints(constraints -> Constraints.apply(constraints, "classpath"))
    );
  }
}
