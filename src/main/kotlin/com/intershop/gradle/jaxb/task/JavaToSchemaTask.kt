/*
 * Copyright 2019 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.intershop.gradle.jaxb.task

import com.intershop.gradle.jaxb.utils.getValue
import com.intershop.gradle.jaxb.utils.setValue
import com.intershop.gradle.jaxb.extension.JaxbExtension
import com.intershop.gradle.jaxb.extension.JaxbExtension.Companion.DEFAULT_SCHEMAGEN_TASK_CLASS_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.withGroovyBuilder
import java.io.File
import javax.inject.Inject

/**
 * This task generates a schema file from
 * existing sources.
 */
abstract class JavaToSchemaTask: DefaultTask() {

    /**
     * Inject service of ObjectFactory (See "Service injection" in Gradle documentation.
     */
    @get:Inject
    abstract val objectFactory: ObjectFactory

    private val outputDirProperty: DirectoryProperty = objectFactory.directoryProperty()
    private val inputDirProperty: DirectoryProperty = objectFactory.directoryProperty()
    private val excludesProperty = objectFactory.listProperty(String::class.java)
    private val includesProperty = objectFactory.listProperty(String::class.java)
    private val namespaceconfigsProperty = objectFactory.mapProperty(String::class.java, String::class.java)
    private val episodeProperty = objectFactory.property(String::class.java)
    private val antTaskClassNameProperty = objectFactory.property(String::class.java)

    init {
        antTaskClassNameProperty.convention(DEFAULT_SCHEMAGEN_TASK_CLASS_NAME)
        episodeProperty.convention("")
    }

    /**
     * Output directory for Schema gen.
     *
     * @property outputDir
     */
    @get:OutputDirectory
    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = outputDirProperty.set(value)

    /**
     * This function set the output directory provider.
     */
    fun provideOutputDir(outputDir: Provider<Directory>) = outputDirProperty.set(outputDir)

    /**
     * Input directory for Schema gen.
     *
     * @property inputDir
     */
    @get:InputDirectory
    var inputDir: File
        get() = inputDirProperty.get().asFile
        set(value) = inputDirProperty.set(value)

    /**
     * This function set the input directory provider.
     */
    fun provideInputDir(inputDir: Provider<Directory>) = inputDirProperty.set(inputDir)

    /**
     * List of excludes patterns for input directory.
     * @property excludes
     */
    @get:Input
    var excludes: List<String>
        get() = excludesProperty.getOrElse(listOf<String>())
        set(value) = excludesProperty.set(value)

    /**
     * This function set the excludes provider.
     */
    fun provideExcludes(excludes: Provider<MutableList<String>>) = excludesProperty.set(excludes)

    /**
     * List of includes patterns for input directory.
     *
     * @property includes
     */
    @get:Input
    var includes: List<String>
        get() = includesProperty.getOrElse(listOf<String>())
        set(value) = includesProperty.set(value)

    /**
     * This function set the includes provider.
     */
    fun provideIncludes(includes: Provider<MutableList<String>>) = includesProperty.set(includes)

    /**
     * Provider for a map of name space configurations.
     *
     * @property namespaceconfigs
     */
    @get:Input
    var namespaceconfigs: Map<String, String>
        get() = namespaceconfigsProperty.getOrElse(mapOf<String, String>())
        set(value) = namespaceconfigsProperty.set(value)

    /**
     * This function set the namespaceconfigs provider.
     */
    fun provideNamespaceconfigs(namespaceconfigs: Provider<MutableMap<String, String>>) =
            namespaceconfigsProperty.set(namespaceconfigs)

    /**
     * Special parameter (episode) see schemagen documentation.
     *
     * @property episode
     */
    @get:Optional
    @get:Input
    var episode: String by episodeProperty

    /**
     * This function set the input property episode.
     */
    fun provideEpisode(episode: Provider<String>) = episodeProperty.set(episode)

    /**
     * This is the classname of javagen.
     *
     * @property antTaskClassName
     */
    @get:Input
    var antTaskClassName: String by antTaskClassNameProperty

    /**
     * This function set the classname of javagen.
     */
    fun provideAntTaskClassName(antTaskClassName: Provider<String>) =
            antTaskClassNameProperty.set(antTaskClassName)

    /**
     * Classpath files for Java code generation (see Jaxb configuration (JAXB_CONFIGURATION_NAME)).
     *
     * @property jaxbClasspathfiles
     */
    @get:Classpath
    val jaxbClasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        returnFiles.from(project.configurations.findByName(JaxbExtension.JAXB_CONFIGURATION_NAME))
        returnFiles
    }

    /**
     * Additional classpath files for Java code generation (see Jaxb configuration (ADD_JAXB_CONFIGURATION_NAME)).
     *
     * @property addjaxbClasspathfiles
     */
    @get:Classpath
    val addjaxbClasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        returnFiles.from(project.configurations.findByName(JaxbExtension.ADD_JAXB_CONFIGURATION_NAME))
        returnFiles
    }

    /**
     * Classpath files for Java code generation.
     *
     * @property classpathfiles
     */
    @get:CompileClasspath
    val classpathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        returnFiles.from(project.configurations.findByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
        returnFiles
    }

    /**
     * This generates a schema files from java
     * source code.
     */
    @TaskAction
    fun generate() {
        val argMap = mutableMapOf<String, String?>(
                "destdir" to outputDir.absolutePath,
                "srcdir" to inputDir.absolutePath,
                "includeantruntime" to "false"
        )

        if (episode.isNotBlank()) {
            argMap["episode"] = episode
        }

        if(project.logger.isInfoEnabled) {
            project.logger.info("Arguments for schema: {}", argMap)
        }

        val classpath = classpathfiles + jaxbClasspathfiles + addjaxbClasspathfiles

        System.setProperty("javax.xml.accessExternalSchema", "all")

        ant.withGroovyBuilder {
            "taskdef"(
                    "name" to "schemagen",
                    "classname" to antTaskClassName,
                    "classpath" to jaxbClasspathfiles.asPath)

            "schemagen"(argMap) {
                namespaceconfigs.forEach {
                    "schema"("namespace" to it.key, "file" to it.value )
                }
                includes.forEach {
                    "include"("name" to it)
                }
                excludes.forEach{
                    "exclude"("name" to it)
                }
                "classpath" {
                    "pathelement"( "path" to classpath.asPath)
                }
            }
        }

        System.clearProperty("javax.xml.accessExternalSchema")
    }
}
