package com.autonomousapps;

import org.gradle.api.artifacts.dsl.DependencyConstraintHandler;

class Constraints {
  static void apply(DependencyConstraintHandler constraints, String on) {
    constraints.add(on, "org.apache.logging.log4j:log4j-core", c -> {
      c.version(v -> {
        v.strictly("[2.16, 3[");
        v.prefer("2.16.0");
      });
      c.because("CVE-2021-44228: Log4j vulnerable to remote code execution");
    });
  }
}
