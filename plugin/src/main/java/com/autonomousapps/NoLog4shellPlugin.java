package com.autonomousapps;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

@SuppressWarnings("unused")
public class NoLog4shellPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project.getDependencies().constraints(constraints -> {
      constraints.add("implementation", "org.apache.logging.log4j:log4j-core", c -> {
        c.version(v -> {
          v.strictly("[2.15, 3[");
          v.prefer("2.15.0");
        });
        c.because("CVE-2021-44228: Log4j vulnerable to remote code execution");
      });
    });
  }
}
