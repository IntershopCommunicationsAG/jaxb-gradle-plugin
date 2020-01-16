/*
 * Copyright 2018 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intershop.gradle.jaxb

import com.intershop.gradle.test.AbstractIntegrationGroovySpec

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS as SUCCESS

class ProjectSpec extends AbstractIntegrationGroovySpec {

    def 'Test xew-test - xjc'() {
        given:
        copyResources('projects/xew-test')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            def xewFile = file('staticfiles/definition/xew-includes.txt')
            def addArgs = ['-Xxew', '-Xxew:instantiate early', '-Xxew:delete', "-Xxew:includeFile \${xewFile.absolutePath.replace('\\\\','/')}"]

            jaxb {
                javaGen {
                    gcdm {
                        binding = file('staticfiles/definition/gcdm-customer-binding.xjb')
                        strictValidation = false
                        extension = true
                        args(addArgs)
                        schemas = fileTree(dir: 'staticfiles/definition', include: '**/**/*.xsd')
                    }
                }
            }

            dependencies {
                jaxbext 'com.github.jaxb-xew-plugin:jaxb-xew-plugin:1.1'
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['jaxb', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenGcdm').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/gcdm/com/customer/gcdm/controller/v3/model/ObjectFactory.java')
        fileExists('build/generated/jaxb/java/gcdm/com/customer/gcdm/controller/v3/model/policy/ObjectFactory.java')
        fileExists('build/generated/jaxb/java/gcdm/com/customer/gcdm/controller/v3/model/crmproxy/ObjectFactory.java')

        where:
        gradleVersion << supportedGradleVersions
    }

    private boolean fileExists(String path) {
        File f = new File(testProjectDir, path)
        return f.isFile() && f.exists()
    }
}
