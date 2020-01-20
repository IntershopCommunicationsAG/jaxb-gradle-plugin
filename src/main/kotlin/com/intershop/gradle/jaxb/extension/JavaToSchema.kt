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
package com.intershop.gradle.jaxb.extension

import com.intershop.gradle.jaxb.utils.getValue
import com.intershop.gradle.jaxb.utils.setValue
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import java.io.File
import javax.inject.Inject

/**
 * This is the extension for schemagen configuration.
 */
abstract class JavaToSchema(val name: String) {

    /**
     * Inject service of ObjectFactory (See "Service injection" in Gradle documentation.
     */
    @get:Inject
    abstract val objectFactory: ObjectFactory

    /**
     * Inject service of ProjectLayout (See "Service injection" in Gradle documentation.
     */
    @get:Inject
    abstract val layout: ProjectLayout

    private val outputDirProperty = objectFactory.directoryProperty()
    private val inputDirProperty = objectFactory.directoryProperty()
    private val excludesProperty = objectFactory.listProperty(String::class.java)
    private val includesProperty = objectFactory.listProperty(String::class.java)
    private val namespaceconfigsProperty = objectFactory.mapProperty(String::class.java, String::class.java)
    private val episodeProperty = objectFactory.property(String::class.java)
    private val antTaskClassNameProperty = objectFactory.property(String::class.java)

    /**
     * Provider for inputDir property.
     */
    val inputDirProvider: Provider<Directory>
        get() = inputDirProperty

    /**
     * Input dir for Schema generation from Java code.
     *
     * @property inputDir
     */
    var inputDir: File
        get() = inputDirProperty.get().asFile
        set(value) = inputDirProperty.set(value)

    /**
     * Provider for excludes property.
     */
    val excludesProvider: Provider<MutableList<String>>
        get() = excludesProperty

    /**
     * Excludes files from input directory.
     *
     * @property excludes
     */
    var excludes by excludesProperty

    /**
     * Add an exclude configuration to the list
     * of excludes.
     *
     * @param exclude
     */
    fun exclude(exclude: String) {
        excludesProperty.add(exclude)
    }

    /**
     * Provider for include property.
     */
    val includesProvider: Provider<MutableList<String>>
        get() = includesProperty

    /**
     * Includes files from input directory.
     *
     * @property excludes
     */
    var includes by includesProperty

    /**
     * Add an include configuration to the list
     * of includes.
     *
     * @param include
     */
    fun include(include: String) {
        includesProperty.add(include)
    }

    /**
     * Provider for a map of name space configurations.
     */
    val namespaceconfigsProvider: Provider<MutableMap<String, String>>
        get() = namespaceconfigsProperty

    /**
     * Contains a map for namespace configs.
     *
     * @property namespaceconfigs
     */
    var namespaceconfigs: Map<String, String>
        get() = namespaceconfigsProperty.getOrElse(mapOf<String, String>())
        set(value) = namespaceconfigsProperty.set(value)


    /**
     * Add an entry to the map.
     *
     * @param key   key for namespace config.
     * @param value value for namespace config.
     */
    fun namespaceconfigs(key: String, value: String) {
        namespaceconfigsProperty.put(key, value)
    }

    /**
     * Add all entries of the input map to the namespaceconfig map.
     *
     * @param map
     */
    fun namespaceconfigs(map: Map<String, String>) {
        namespaceconfigsProperty.putAll(map)
    }

    /**
     * Provider for episode property.
     */
    val episodeProvider: Provider<String>
        get() = episodeProperty

    /**
     * Special parameter (episode) see schemagen documentation.
     *
     * @property episode
     */
    var episode: String by episodeProperty

    /**
     * Provider for outputDir property.
     */
    val outputDirProvider: Provider<Directory>
        get() = outputDirProperty

    /**
     * Output dir for Schema generation from Java code.
     *
     * @property outputDir
     */
    var outputDir: File
        get() = outputDirProperty.get().asFile
        set(value) = outputDirProperty.set(value)

    /**
     * Provider for used JavaGen antTaskClassName.
     */
    val antTaskClassNameProvider: Provider<String>
        get() = antTaskClassNameProperty

    /**
     * Parameter antTaskClassName for Gradle SchemaGen task.
     *
     * @property antTaskClassName
     */
    var antTaskClassName: String by antTaskClassNameProperty

    init {
        val outPath = "${JaxbExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${JaxbExtension.JAXB_SCHEMAGEN_OUTPUTPATH}"
        outputDirProperty.convention(layout.buildDirectory.
                        dir("${outPath}/${name.replace(' ', '_')}").get())
        antTaskClassNameProperty.convention("com.sun.tools.jxc.SchemaGenTask")
        includesProperty.add("**/**/*.java")
        episodeProperty.convention("")
    }

    /**
     * Calculates the task name.
     *
     * @property taskName name with prefix jaxbSchemaGen
     */
    val taskName = "jaxbSchemaGen" + name.capitalize()
}
