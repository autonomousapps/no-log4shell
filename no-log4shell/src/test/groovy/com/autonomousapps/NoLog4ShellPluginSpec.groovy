package com.autonomousapps

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class NoLog4ShellPluginSpec extends Specification {

  @TempDir File testProjectDir
  private File settingsFile
  private File buildFile

  def setup() {
    settingsFile = new File(testProjectDir, 'settings.gradle')
    buildFile = new File(testProjectDir, 'build.gradle')
  }

  def "projects are not vulnerable to this particular rce exploit"() {
    given:
    settingsFile << "rootProject.name = 'juicy-target'"
    buildFile << """\
      plugins {
        id 'java-library'
        id 'com.autonomousapps.no-log4shell'
      }
      
      repositories {
        mavenCentral()
      }
      
      dependencies {
        implementation 'org.apache.logging.log4j:log4j-core:2.14.1'
      }
    """.stripIndent()

    when:
    def result = GradleRunner.create()
      .forwardOutput()
      .withPluginClasspath()
      .withProjectDir(testProjectDir)
      .withArguments('dependencies', '--configuration', 'runtimeClasspath')
      .build()

    then: 'The constraint is applied transitively'
    result.output.contains('org.apache.logging.log4j:log4j-core:2.14.1 -> 2.16.0')
    result.output.contains('org.apache.logging.log4j:log4j-api:2.16.0')
    result.output.contains('org.apache.logging.log4j:log4j-core:{strictly [2.16, 3[; prefer 2.16.0} -> 2.16.0 (c)')
  }

  def "projects that don't apply this plugin are vulnerable to this rce exploit"() {
    given:
    settingsFile << "rootProject.name = 'juicy-target'"
    buildFile << """\
      plugins {
        id 'java-library'
      }
      
      repositories {
        mavenCentral()
      }
      
      dependencies {
        implementation 'org.apache.logging.log4j:log4j-core:2.14.1'
      }
    """.stripIndent()

    when:
    def result = GradleRunner.create()
      .forwardOutput()
      .withProjectDir(testProjectDir)
      .withArguments('dependencies', '--configuration', 'runtimeClasspath')
      .build()

    then: 'Oh no!'
    result.output.contains('org.apache.logging.log4j:log4j-core:2.14.1\n')
  }

  def "settings scripts are not vulnerable to this particular rce exploit"() {
    given:
    settingsFile << """\
      plugins {
        id 'com.autonomousapps.no-log4shell-settings'
      }
      rootProject.name = 'juicy-target'
    """.stripIndent()
    buildFile << """\
      plugins {
        // org.apache.logging.log4j:log4j-core:2.14.1 is a dependency
        id 'com.github.johnrengelman.shadow' version '7.0.0'
      }
    """.stripIndent()

    when:
    def result = GradleRunner.create()
      .withPluginClasspath()
      .forwardOutput()
      .withGradleVersion('7.3')
      .withProjectDir(testProjectDir)
      .withArguments('buildEnvironment')
      .build()

    then: 'The constraint is applied transitively'
    result.output.contains('org.apache.logging.log4j:log4j-core:2.14.1 -> 2.16.0\n')
    result.output.contains('org.apache.logging.log4j:log4j-core:{strictly [2.16, 3[; prefer 2.16.0} -> 2.16.0 (c)\n')
  }

  def "settings scripts that don't apply this plugin are vulnerable to this rce exploit"() {
    given:
    settingsFile << "rootProject.name = 'juicy-target'"
    buildFile << """\
      plugins {
        id 'com.github.johnrengelman.shadow' version '7.0.0'
      }
    """.stripIndent()

    when:
    def result = GradleRunner.create()
      .forwardOutput()
      .withGradleVersion('7.3')
      .withProjectDir(testProjectDir)
      .withArguments('buildEnvironment')
      .build()

    then: 'Oh no!'
    result.output.contains('org.apache.logging.log4j:log4j-core:2.14.1\n')
  }

  def "settings scripts shouldn't apply plugin to Gradle >= 7.3.2"() {
    given:
    settingsFile << """\
      plugins {
        id 'com.autonomousapps.no-log4shell-settings'
      }
      rootProject.name = 'juicy-target'
    """.stripIndent()
    buildFile << """\
      plugins {
        // org.apache.logging.log4j:log4j-core:2.14.1 is a dependency
        id 'com.github.johnrengelman.shadow' version '7.0.0'
      }
    """.stripIndent()

    when:
    def result = GradleRunner.create()
      .forwardOutput()
      .withProjectDir(testProjectDir)
      .withPluginClasspath()
      .withGradleVersion('7.3.2')
      .withArguments('buildEnvironment')
      .buildAndFail()

    then:
    result.output.contains('This plugin is unnecessary if using Gradle >= 7.3.2.')
  }
}
