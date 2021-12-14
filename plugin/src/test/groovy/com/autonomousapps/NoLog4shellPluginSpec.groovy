package com.autonomousapps

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class NoLog4shellPluginSpec extends Specification {

  @TempDir File testProjectDir
  private File settingsFile
  private File buildFile

  def setup() {
    settingsFile = new File(testProjectDir, 'settings.gradle')
    buildFile = new File(testProjectDir, 'build.gradle')
  }

  def "not vulnerable to this particular rce exploit"() {
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
    result.output.contains('org.apache.logging.log4j:log4j-core:2.14.1 -> 2.15.0')
    result.output.contains('org.apache.logging.log4j:log4j-api:2.15.0')
    result.output.contains(
      'org.apache.logging.log4j:log4j-core:{strictly [2.15, 3[; prefer 2.15.0} -> 2.15.0 (c)')
  }

  def "builds that don't apply this plugin are vulnerable to this rce exploit"() {
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
}
