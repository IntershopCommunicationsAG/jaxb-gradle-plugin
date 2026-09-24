import org.asciidoctor.gradle.jvm.AsciidoctorTask
import io.gitee.pkmer.enums.PublishingType

/*
 * Copyright 2022 Intershop Communications AG.
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
 * limitations under the License.
 */

plugins {
    `jvm-test-suite`

    // project plugins
    groovy

    kotlin("jvm") version "2.4.20"

    // test coverage
    jacoco

    // ide plugin
    idea

    // artifact signing - necessary on Maven Central
    signing

    // plugin for documentation
    // NOTE: 4.0.5 (Aug 2025) is the latest release; its internal 'grolifant' library still calls the
    // deprecated StartParameter.isConfigurationCacheRequested, which will be removed in Gradle 10.
    // There is no alternative plugin (the xbib fork is broken on Gradle 9, all other asciidoc
    // plugins are generators, not renderers). An org.asciidoctor 5.0.0-alpha.1 line exists since
    // Sep 2025, so a final 5.x is expected to be available by the time Gradle 10 is released -
    // upgrade to it then.
    id("org.asciidoctor.jvm.convert") version "4.0.5"

    // documentation
    id("org.jetbrains.dokka-javadoc") version "2.2.0"

    // plugin for publishing to Gradle Portal
    id("com.gradle.plugin-publish") version "2.2.1"

    id("com.dorongold.task-tree") version "4.0.2"

    id("io.gitee.pkmer.pkmerboot-central-publisher") version "1.1.1"
}

// release configuration
group = "com.intershop.gradle.jaxb"
description = "Gradle JAXB code generation plugin"
// apply gradle property 'projectVersion' to project.version, default to 'LOCAL'
val projectVersion = project.findProperty("projectVersion") as String?
version = projectVersion ?: "LOCAL"

val sonatypeUsername = project.findProperty("sonatypeUsername") as String?
val sonatypePassword = project.findProperty("sonatypePassword") as String?

repositories {
    mavenCentral()
    mavenLocal()
}

val pluginUrl = "https://github.com/IntershopCommunicationsAG/${project.name}"
gradlePlugin {
    website = pluginUrl
    vcsUrl = pluginUrl

    plugins {
        create(project.name) {
            id = "com.intershop.gradle.jaxb"
            implementationClass = "com.intershop.gradle.jaxb.JaxbPlugin"
            displayName = project.name
            description = project.description
            tags = listOf("intershop", "jaxb", "build", "code", "generator")
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// set correct project status
if (project.version.toString().endsWith("-SNAPSHOT")) {
    status = "snapshot"
}

/*
 * Gradle 9.7.1 bundles Groovy 4.0.32 and 'gradleTestKit()' puts the whole Gradle distribution -
 * including that bundled groovy jar - on the compile classpath. The Groovy plugin's automatic
 * groovyClasspath inference therefore picks up Groovy 4, which makes Spock's global AST transform
 * (spock-bom 2.4-groovy-5.0, pulled in via test-gradle-plugin) abort with
 * IncompatibleGroovyVersionException.
 *
 * Fix: use a dedicated, isolated configuration that contains *only* Groovy 5 as the compiler
 * classpath, so the Groovy compiler and Spock's AST transform both see Groovy 5.
 */
val groovyCompiler: Configuration = configurations.create("groovyCompiler") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

tasks.withType<GroovyCompile>().configureEach {
    groovyClasspath = groovyCompiler
}

testing {
    suites.withType<JvmTestSuite> {
        useSpock()

        dependencies {
            implementation("com.intershop.gradle.test:test-gradle-plugin:7.0.0")
            implementation(gradleTestKit())
        }

        targets {
            all {
                testTask.configure {
                    systemProperty("intershop.gradle.versions", "8.5,8.10.2,9.1.0,9.7.1")

                    testLogging {
                        showStandardStreams = true
                        showStackTraces = true
                        events(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
                    }

                    if(project.hasProperty("repoURL")
                        && project.hasProperty("repoUser")
                        && project.hasProperty("repoPasswd")) {
                        systemProperty("repo_url_config", project.property("repoURL").toString())
                        systemProperty("repo_user_config", project.property("repoUser").toString())
                        systemProperty("repo_passwd_config", project.property("repoPasswd").toString())
                    }
                }
            }
        }
    }
}

val buildDir = layout.buildDirectory.asFile.get()
tasks {
    register<Copy>("copyAsciiDoc") {
        includeEmptyDirs = false

        val outputDir = project.layout.buildDirectory.dir("tmp/asciidoctorSrc")
        val inputFiles = fileTree(rootDir) {
            include("**/*.asciidoc")
            exclude("build/**")
        }

        inputs.files.plus( inputFiles )
        outputs.dir( outputDir )

        doFirst {
            outputDir.get().asFile.mkdir()
        }

        from(inputFiles)
        into(outputDir)
    }

    withType<AsciidoctorTask> {
        dependsOn("copyAsciiDoc")

        setSourceDir(project.layout.buildDirectory.dir("tmp/asciidoctorSrc"))
        sources(delegateClosureOf<PatternSet> {
            include("README.asciidoc")
        })

        outputOptions {
            setBackends(listOf("html5", "docbook"))
        }

        setOptions(mapOf(
            "doctype"               to "article",
            "ruby"                  to "erubis"
        ))
        setAttributes(mapOf(
            "latestRevision"        to project.version,
            "toc"                   to "left",
            "toclevels"             to "2",
            "source-highlighter"    to "coderay",
            "icons"                 to "font",
            "setanchors"            to "true",
            "idprefix"              to "asciidoc",
            "idseparator"           to "-",
            "docinfo1"              to "true"
        ))
    }

    withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)

            html.outputLocation.set( File(buildDir, "jacocoHtml") )
        }

        dependsOn(test)
    }

    jar.configure {
        dependsOn(asciidoctor)
    }

    withType<Sign> {
        val sign = this
        withType<PublishToMavenLocal> {
            this.dependsOn(sign)
        }
        withType<PublishToMavenRepository> {
            this.dependsOn(sign)
        }
    }

    afterEvaluate {
        getByName<Jar>("javadocJar") {
            dependsOn(dokkaGenerate)
            from(dokkaGeneratePublicationJavadoc)
        }
    }
}

val stagingRepoDir = project.layout.buildDirectory.dir("stagingRepo")

publishing {
    publications {
        create("intershopMvn", MavenPublication::class.java) {

            from(components["java"])

            artifact(File(buildDir, "docs/asciidoc/html5/README.html")) {
                classifier = "reference"
            }

            artifact(File(buildDir, "docs/asciidoc/docbook/README.xml")) {
                classifier = "docbook"
            }
        }
        withType<MavenPublication>().configureEach {
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set(pluginUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                organization {
                    name.set("Intershop Communications AG")
                    url.set("http://intershop.com")
                }
                developers {
                    developer {
                        id.set("m-raab")
                        name.set("M. Raab")
                        email.set("mraab@intershop.de")
                    }
                }
                scm {
                    connection.set("git@github.com:IntershopCommunicationsAG/${project.name}.git")
                    developerConnection.set("git@github.com:IntershopCommunicationsAG/${project.name}.git")
                    url.set(pluginUrl)
                }
            }
        }
    }
    repositories {
        maven {
            name = "LOCAL"
            url = stagingRepoDir.get().asFile.toURI()
        }
    }
}

pkmerBoot {
    sonatypeMavenCentral{
        // the same with publishing.repositories.maven.url in the configuration.
        stagingRepository = stagingRepoDir

        /**
         * get username and password from
         * <a href="https://central.sonatype.com/account"> central sonatype account</a>
         */
        username = sonatypeUsername
        password = sonatypePassword

        // Optional the publishingType default value is PublishingType.AUTOMATIC
        publishingType = PublishingType.USER_MANAGED
    }
}

signing {
    sign(publishing.publications["intershopMvn"])
}

// dependency versions
val groovyVersion = "5.1.2"

dependencies {
    // NOTE: neither gradleApi() nor gradleKotlinDsl() may be an 'implementation' dependency here.
    // The 'java-gradle-plugin' plugin (applied by 'com.gradle.plugin-publish') already provides the
    // Gradle API for compilation - but NOT the Gradle Kotlin DSL. As 'implementation' they
    // additionally put the *current* Gradle distribution jars (gradle-api-<version>.jar,
    // <dist>/lib/*) on the runtime classpath, from which 'pluginUnderTestMetadata' derives the
    // classpath TestKit injects into every test build via withPluginClasspath(). Older Gradle
    // versions under test (8.5, 8.10.2) cannot instrument those 9.x jars and fail with
    // "Failed to create Jar file ... gradle-api-9.7.1.jar"
    // (verified: 84 of 172 tests fail when gradleKotlinDsl() is an 'implementation' dependency).
    //
    // gradleKotlinDsl() is therefore split into the two scopes that actually need it:
    // - compileOnly: SchemaToJavaTask/JavaToSchemaTask import org.gradle.kotlin.dsl.withGroovyBuilder
    //   (verified: compilation fails with "Unresolved reference 'withGroovyBuilder'" without it).
    // - testImplementation: the in-JVM ProjectBuilder specs instantiate those tasks directly and
    //   need GroovyBuilderScope at runtime. The test classpath does not feed pluginUnderTestMetadata,
    //   so this is leak-free. Real TestKit builds get the Kotlin DSL from their own distribution.
    compileOnly(gradleKotlinDsl())
    testImplementation(gradleKotlinDsl())

    // isolated Groovy compiler classpath - see the groovyCompiler configuration above
    groovyCompiler(platform("org.apache.groovy:groovy-bom:$groovyVersion"))
    groovyCompiler("org.apache.groovy:groovy")
    groovyCompiler("org.apache.groovy:groovy-ant")
    groovyCompiler("org.apache.groovy:groovy-json")
    groovyCompiler("org.apache.groovy:groovy-xml")
    groovyCompiler("org.apache.groovy:groovy-templates")
}
