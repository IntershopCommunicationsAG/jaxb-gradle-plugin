/*
 * Copyright 2015 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.intershop.gradle.jaxb

import com.intershop.gradle.jaxb.extension.JaxbExtension
import com.intershop.gradle.test.AbstractIntegrationSpec

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS as SUCCESS

class SamplesSpec extends AbstractIntegrationSpec {

    def 'Test multithread execution - xjc'() {
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
                        packageName = ''
                    }
                    test02 {
                        packageName = 'primer.po'
                        schema = file('test02/po.xsd')
                    }
                    test03 {
                        binding = file('test03/binding.xjb')
                        schema = file('test03/po.xsd')
                    }
                    test04 {
                        binding = file('test04/binding.xjb')
                        schema = file('test04/example.xsd')
                    }
                    test05 {
                        schema = file('test05/po.xsd')
                        packageName = 'primer.test05.myPo'
                        extension = true
                        arg('-Xsync-methods')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i', '--configure-on-demand', '--parallel', '--max-workers=4']

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

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/Foo.java')
        fileExists('build/generated/jaxb/java/test/ObjectFactory.java')
        fileExists('build/classes/main/Foo.class')
        fileExists('build/classes/main/ObjectFactory.class')

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

            dependencies {
                compile 'com.sun.xml.bind:jaxb-core:${JaxbExtension.XJC_DEFAULT_VERSION}'
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/simple/ObjectFactory.java')
        fileExists('build/classes/main/simple/ObjectFactory.class')

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

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/po/PurchaseOrderType.java')
        fileExists('build/generated/jaxb/java/test/primer/po/USAddress.java')
        fileExists('build/classes/main/primer/po/PurchaseOrderType.class')
        fileExists('build/classes/main/primer/po/USAddress.class')

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
                    test {
                        schema = file('nameCard.dtd')
                        binding = file('nameCard.jaxb')
                        arg('-dtd')
                        packageName = ''
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/NameCard.java')
        fileExists('build/generated/jaxb/java/test/NameCards.java')
        fileExists('build/classes/main/NameCard.class')
        fileExists('build/classes/main/NameCards.class')

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

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

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
        fileExists('build/classes/main/primer/myPo/ObjectFactory.class')
        fileExists('build/classes/main/primer/myPo/USAddress.class')
        fileExists('build/classes/main/primer/myPo/USState.class')

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
                    test {
                        binding = file('binding.xjb')
                        schema = file('example.xsd')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/example/Clazz.java')
        fileExists('build/generated/jaxb/java/test/example/FooBar.java')
        fileExists('build/generated/jaxb/java/test/example/ObjectFactory.java')
        fileExists('build/classes/main/example/Clazz.class')
        fileExists('build/classes/main/example/FooBar.class')
        fileExists('build/classes/main/example/ObjectFactory.class')

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
                    test {
                        schema = file('po.xsd')
                        packageName = 'primer.myPo'
                        extension = true
                        arg('-Xlocator')
                    }
                }
            }

            dependencies {
                compile 'com.sun.xml.bind:jaxb-core:${JaxbExtension.XJC_DEFAULT_VERSION}'
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/main/primer/myPo/USAddress.class')

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
                    test {
                        schema = file('po.xsd')
                        packageName = 'primer.myPo'
                        extension = true
                        arg('-Xsync-methods')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/main/primer/myPo/USAddress.class')

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

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/com/example/ipo/ObjectFactory.java')
        fileExists('build/classes/main/com/example/ipo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/Address.java')
        fileExists('build/classes/main/com/example/ipo/Address.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/Items.java')
        fileExists('build/classes/main/com/example/ipo/Items.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/PurchaseOrderType.java')
        fileExists('build/classes/main/com/example/ipo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/UKAddress.java')
        fileExists('build/classes/main/com/example/ipo/UKAddress.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USAddress.java')
        fileExists('build/classes/main/com/example/ipo/USAddress.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USState.java')
        fileExists('build/classes/main/com/example/ipo/USState.class')
        fileExists('build/generated/jaxb/java/test/com/example/ipo/USTaxExemptPurchaseOrderType.java')
        fileExists('build/classes/main/com/example/ipo/USTaxExemptPurchaseOrderType.class')

        where:
        gradleVersion << supportedGradleVersions
    }

    def 'Test ubl - xjc'() {
        given:
        copyResources('samples/ubl')

        buildFile << """
            plugins {
                id 'java'
                id 'com.intershop.gradle.jaxb'
            }

            jaxb {
                javaGen {
                    test {
                        binding = file('ubl.xjb')
                        schemas = fileTree(dir: 'cd-UBL-1.0/xsd', include: '**/*.xsd')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-i']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/org/oasis/ubl/order/ObjectFactory.java')
        fileExists('build/classes/main/org/oasis/ubl/order/ObjectFactory.class')
        dirExists('build/generated/jaxb/java/test/org/oasis/ubl/codelist')
        dirExists('build/classes/main/org/oasis/ubl/codelist')
        dirExists('build/generated/jaxb/java/test/org/oasis/ubl/orderchange')
        dirExists('build/classes/main/org/oasis/ubl/orderchange')

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

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['compileJava', '-s', '-d']

        def result = getPreparedGradleRunner()
                .withArguments(args)
                .withGradleVersion(gradleVersion)
                .build()

        then:
        result.task(':jaxbJavaGenTest').outcome == SUCCESS
        result.task(':compileJava').outcome == SUCCESS

        fileExists('build/generated/jaxb/java/test/primer/myPo/ObjectFactory.java')
        fileExists('build/classes/main/primer/myPo/ObjectFactory.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/Items.java')
        fileExists('build/classes/main/primer/myPo/Items.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/PurchaseOrderType.java')
        fileExists('build/classes/main/primer/myPo/PurchaseOrderType.class')
        fileExists('build/generated/jaxb/java/test/primer/myPo/USAddress.java')
        fileExists('build/classes/main/primer/myPo/USAddress.class')

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
                        javaFiles = fileTree(dir: 'src', include: '**/**/*.java', exclude: 'Main.java')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['jaxb', '-s', '-d']

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
                        javaFiles = fileTree(dir: 'src', include: '**/**/*.java', exclude: 'Main.java')
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['jaxb', '-s', '-d']

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
                        javaFiles = fileTree(dir: 'src',
                                             include: '**/**/*.java',
                                             excludes: [ 'Main.java', 'shoppingCart/AdapterPurchaseListToHashMap.java'] )
                    }
                }
            }

            repositories {
                mavenCentral()
            }
        """.stripIndent()

        when:
        List<String> args = ['jaxb', '-s', '-d']

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
        schemaFile.text.contains('KitchenWorldBasketType')
        schemaFile.text.contains('PurchaseListType')

        where:
        gradleVersion << supportedGradleVersions
    }

    private boolean fileExists(String path) {
        File f = new File(testProjectDir, path)
        return f.isFile() && f.exists()
    }

    private boolean dirExists(String path) {
        File f = new File(testProjectDir, path)
        return f.isDirectory() && f.exists()
    }
}
