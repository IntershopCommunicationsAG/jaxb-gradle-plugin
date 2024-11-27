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

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class SamplesSpec extends AbstractIntegrationGroovySpec {

    private String DEPENDENCIES = """
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
                implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
            }
    """.stripIndent()

    private String TASK_JAVA_COMPILE_CONFIGURATION = """
            tasks.withType(JavaCompile) {
                options.compilerArgs += ['-Xlint:deprecation']
                options.compilerArgs += ['-Xlint:unchecked']
            }
    """.stripIndent()

    def 'Test multithreading execution - xjc'() {
        given:
        copyResources('samples/catalog-resolver', 'test01')
        copyResources('samples/create-marshal', 'test02')
        copyResources('samples/external-customize', 'test03')
        copyResources('samples/fix-collides', 'test04')
        copyResources('samples/synchronized-methods', 'test05')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test01 {
                        schema = file('test01/test.xsd')
                        catalog = file('test01/catalog.cat')
                        packageName = 'test01'
                    }
                    test02 {
                        packageName = 'primer.po'
                        schema = file('test02/po.xsd')
                    }
                    test03 {
                        binding = file('test03/binding.xjb')
                        schema = file('test03/po.xsd')
                        packageName = 'test03'
                    }
                    test04 {
                        binding = file('test04/binding.xjb')
                        schema = file('test04/example.xsd')
                        packageName = 'test04'
                    }
                    test05 {
                        schema = file('test05/po.xsd')
                        packageName = 'primer.test05.myPo'
                        extension = true
                        arg('-Xsync-methods')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest01').outcome == SUCCESS
        result.task(':jaxbJavaGenTest02').outcome == SUCCESS
        result.task(':jaxbJavaGenTest03').outcome == SUCCESS
        result.task(':jaxbJavaGenTest04').outcome == SUCCESS
        result.task(':jaxbJavaGenTest05').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        when:
        def resultSec = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        resultSec.task(':jaxbJavaGenTest01').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest02').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest03').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest04').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest05').outcome == UP_TO_DATE
        resultSec.task(':compileJava').outcome == UP_TO_DATE

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test multithreading execution - xjc with debug'() {
        given:
        copyResources('samples/catalog-resolver', 'test01')
        copyResources('samples/create-marshal', 'test02')
        copyResources('samples/external-customize', 'test03')
        copyResources('samples/fix-collides', 'test04')
        copyResources('samples/synchronized-methods', 'test05')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test01 {
                        schema = file('test01/test.xsd')
                        catalog = file('test01/catalog.cat')
                        packageName = 'test01'
                    }
                    test02 {
                        packageName = 'primer.po'
                        schema = file('test02/po.xsd')
                    }
                    test03 {
                        binding = file('test03/binding.xjb')
                        schema = file('test03/po.xsd')
                        packageName = 'test03'
                    }
                    test04 {
                        binding = file('test04/binding.xjb')
                        schema = file('test04/example.xsd')
                        packageName = 'test04'
                    }
                    test05 {
                        schema = file('test05/po.xsd')
                        packageName = 'primer.test05.myPo'
                        extension = true
                        arg('-Xsync-methods')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }
            
            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '--configure-on-demand', '--parallel', '--max-workers=4']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest01').outcome == SUCCESS
        result.task(':jaxbJavaGenTest02').outcome == SUCCESS
        result.task(':jaxbJavaGenTest03').outcome == SUCCESS
        result.task(':jaxbJavaGenTest04').outcome == SUCCESS
        result.task(':jaxbJavaGenTest05').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        when:
        def resultSec = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        resultSec.task(':jaxbJavaGenTest01').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest02').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest03').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest04').outcome == UP_TO_DATE
        resultSec.task(':jaxbJavaGenTest05').outcome == UP_TO_DATE
        resultSec.task(':compileJava').outcome == UP_TO_DATE

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test catalog-resolver - xjc'() {
        given:
        copyResources('samples/catalog-resolver')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('test.xsd')
                        catalog = file('catalog.cat')
                        packageName = ''
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }
            
            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/generated/Foo.java')
        fileExists('build/generated/jaxb/java/test/generated/ObjectFactory.java')
        fileExists('build/classes/java/main/generated/Foo.class')
        fileExists('build/classes/java/main/generated/ObjectFactory.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test catalog-resolver with different build dir - xjc'() {
        given:
        copyResources('samples/catalog-resolver')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('test.xsd')
                        catalog = file('catalog.cat')
                        packageName = ''
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }
            
            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/generated/Foo.java')
        fileExists('build/generated/jaxb/java/test/generated/ObjectFactory.java')
        fileExists('build/classes/java/main/generated/Foo.class')
        fileExists('build/classes/java/main/generated/ObjectFactory.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test character-escape - xjc'() {
        given:
        copyResources('samples/character-escape')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        packageName = 'simple'
                        schema = file('simple.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/simple/ObjectFactory.java')
        fileExists('build/classes/java/main/simple/ObjectFactory.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test create-marshal - xjc'() {
        given:
        copyResources('samples/create-marshal')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        packageName = 'primer.po'
                        schema = file('po.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/po/PurchaseOrderType.java')
        fileExists('build/generated/jaxb/java/test/primer/po/USAddress.java')
        fileExists('build/classes/java/main/primer/po/PurchaseOrderType.class')
        fileExists('build/classes/java/main/primer/po/USAddress.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test dtd - xjc'() {
        given:
        copyResources('samples/dtd')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    testxjc {
                        schema = file('nameCard.dtd')
                        binding = file('nameCard.jaxb')
                        arg('-dtd')
                        packageName = ''
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTestxjc').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists("build/generated/jaxb/java/testxjc/foo/jaxb/NameCard.java")
        fileExists("build/generated/jaxb/java/testxjc/foo/jaxb/NameCards.java")
        fileExists("build/classes/java/main/foo/jaxb/NameCard.class")
        fileExists("build/classes/java/main/foo/jaxb/NameCards.class")

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test external-customize - xjc'() {
        given:
        copyResources('samples/external-customize')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        binding = file('binding.xjb')
                        schema = file('po.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USState.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')
        fileExists('build/classes/java/main/primer/myPo/USState.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test fix-collides - xjc'() {
        given:
        copyResources('samples/fix-collides')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    testxjc {
                        binding = file('binding.xjb')
                        schema = file('example.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTestxjc').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/testxjc/example/Clazz.java')
        fileExists('build/generated/jaxb/java/testxjc/example/FooBar.java')
        fileExists('build/generated/jaxb/java/testxjc/example/ObjectFactory.java')
        fileExists('build/classes/java/main/example/Clazz.class')
        fileExists('build/classes/java/main/example/FooBar.class')
        fileExists('build/classes/java/main/example/ObjectFactory.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test locator-support - xjc'() {
        given:
        copyResources('samples/locator-support')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    testxjc {
                        schema = file('po.xsd')
                        packageName = 'primer.myPo'
                        extension = true
                        arg('-Xlocator')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}

            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTestxjc').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/Items.java')
        fileExists('build/classes/java/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/java/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/USAddress.java')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test synchronized-methods - xjc'() {
        given:
        copyResources('samples/synchronized-methods')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    testxjc {
                        schema = file('po.xsd')
                        packageName = 'primer.myPo'
                        extension = true
                        arg('-Xsync-methods')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTestxjc').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/Items.java')
        fileExists('build/classes/java/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/java/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/testxjc/primer/myPo/USAddress.java')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test type-substitution - xjc'() {
        given:
        copyResources('samples/type-substitution')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        catalog = file('catalog.cat')
                        schemas = fileTree(dir: '.', includes: ['ipo.xsd', 'ustaxexemptpo.xsd'])
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/com/example/ipo/ObjectFactory.java')
        fileExists('build/classes/java/main/com/example/ipo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/Address.java')
        fileExists('build/classes/java/main/com/example/ipo/Address.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/Items.java')
        fileExists('build/classes/java/main/com/example/ipo/Items.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/PurchaseOrderType.java')
        fileExists('build/classes/java/main/com/example/ipo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/UKAddress.java')
        fileExists('build/classes/java/main/com/example/ipo/UKAddress.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USAddress.java')
        fileExists('build/classes/java/main/com/example/ipo/USAddress.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USState.java')
        fileExists('build/classes/java/main/com/example/ipo/USState.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USTaxExemptPurchaseOrderType.java')
        fileExists('build/classes/java/main/com/example/ipo/USTaxExemptPurchaseOrderType.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test vendor-extensions - xjc'() {
        given:
        copyResources('samples/vendor-extensions')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('po.xsd')
                        packageName = 'primer.myPo'
                        extension = true
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            ${DEPENDENCIES}

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/java/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/java/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test j2s-create-marshal - schemagen'() {
        given:
        copyResources('samples/j2s-create-marshal')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                schemaGen {
                    test {
                        inputDir = file('src')
                        excludes = [ 'Main.java' ]
                    }
                }
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

        File schemaFile = new File(testProjectDir, 'build/generated/jaxb/schema/test/schema1.xsd')

        then:
        result.task(':jaxbSchemaGenTest').outcome == SUCCESS

        schemaFile.exists()
        ! schemaFile.text.contains('name="main"')
        schemaFile.text.contains('address')
        schemaFile.text.contains('businessCard')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test j2s-xmlAccessorOrder - schemagen'() {
        given:
        copyResources('samples/j2s-xmlAccessorOrder')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                schemaGen {
                    test {
                        inputDir = file('src')
                        excludes = [ 'Main.java' ]
                    }
                }
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

        File schemaFile = new File(testProjectDir, 'build/generated/jaxb/schema/test/schema1.xsd')

        then:
        result.task(':jaxbSchemaGenTest').outcome == SUCCESS

        schemaFile.exists()
        ! schemaFile.text.contains('name="main"')
        schemaFile.text.contains('usAddress')
        schemaFile.text.contains('purchaseOrder')
        schemaFile.text.contains('PurchaseOrderType')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test j2s-xmlAdapter - schemagen'() {
        given:
        copyResources('samples/j2s-xmlAdapter')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                schemaGen {
                    test {
                        inputDir = file('src')
                        excludes = [ 'Main.java', 'shoppingCart/AdapterPurchaseListToHashMap.java']
                    }
                }
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

        File schemaFile = new File(testProjectDir, 'build/generated/jaxb/schema/test/schema1.xsd')

        then:
        result.task(':jaxbSchemaGenTest').outcome == SUCCESS

        schemaFile.exists()
        ! schemaFile.text.contains('name="main"')
        schemaFile.text.contains('kitchenWorldBasket')
        schemaFile.text.contains('purchaseList')

       where:
       gradleVersion << supportedGradleVersions
    }

    def 'Test - datatypeconverter - xjc'() {
        given:
        copyResources('samples/datatypeconverter')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('po.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/java/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/POType.java')
        fileExists('build/classes/java/main/primer/myPo/POType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USState.java')
        fileExists('build/classes/java/main/primer/myPo/USState.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test - element-substitution - xjc'() {
        given:
        copyResources('samples/element-substitution')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('folder.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/org/example/ObjectFactory.java')
        fileExists('build/classes/java/main/org/example/ObjectFactory.class')

        fileExists('build/generated/jaxb/java/test/org/example/BidHeader.java')
        fileExists('build/classes/java/main/org/example/BidHeader.class')
        fileExists('build/generated/jaxb/java/test/org/example/Folder.java')
        fileExists('build/classes/java/main/org/example/Folder.class')
        fileExists('build/generated/jaxb/java/test/org/example/Header.java')
        fileExists('build/classes/java/main/org/example/Header.class')
        fileExists('build/generated/jaxb/java/test/org/example/InvoiceHeader.java')
        fileExists('build/classes/java/main/org/example/InvoiceHeader.class')
        fileExists('build/generated/jaxb/java/test/org/example/OrderHeader.java')
        fileExists('build/classes/java/main/org/example/OrderHeader.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test - inline-customize - xjc'() {
        given:
        copyResources('samples/inline-customize')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('po.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/java/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/java/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/POType.java')
        fileExists('build/classes/java/main/primer/myPo/POType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/java/main/primer/myPo/USAddress.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USState.java')
        fileExists('build/classes/java/main/primer/myPo/USState.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test - modify-marshal - xjc'() {
        given:
        copyResources('samples/modify-marshal')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        schema = file('po.xsd')
                    }
                }
            }
            
            ${TASK_JAVA_COMPILE_CONFIGURATION}
            
            dependencies {
                implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/generated/ObjectFactory.java')
        fileExists('build/classes/java/main/generated/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/generated/Items.java')
        fileExists('build/classes/java/main/generated/Items.class')
        fileExists('build/generated/jaxb/java/test/generated/PurchaseOrderType.java')
        fileExists('build/classes/java/main/generated/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/generated/USAddress.java')
        fileExists('build/classes/java/main/generated/USAddress.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def "the plugin supports configuration cache"() {
        given:
        copyResources('samples/inline-customize')

        buildFile << """
        plugins {
            id 'java'
            id 'com.intershop.gradle.jaxb'
        }

        jaxb {
            javaGen {
                test {
                    schema = file('po.xsd')
                }
            }
        }

        ${DEPENDENCIES}

        repositories {
            mavenCentral()
        }
        """

        when:
        getPreparedGradleRunner()
            .withArguments('--configuration-cache', 'jaxb')
            .build()

        and:
        def result = getPreparedGradleRunner()
            .withArguments('--configuration-cache', 'jaxb')
            .build()

        then:
        result.output.contains('Reusing configuration cache.')
    }

    private boolean fileExists(String path) {
        File f = new File(testProjectDir, path)
        return f.isFile() && f.exists()
    }
}
