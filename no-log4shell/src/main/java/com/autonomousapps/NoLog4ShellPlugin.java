package com.autonomousapps;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

@SuppressWarnings("unused")
public class NoLog4ShellPlugin implements Plugin<Project> {
  public void apply(Project project) {
    project
        .getDependencies()
        .constraints(constraints -> Constraints.apply(constraints, "implementation"));
  }
}
