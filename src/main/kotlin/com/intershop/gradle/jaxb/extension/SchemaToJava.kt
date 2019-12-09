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
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import java.io.File
import javax.inject.Inject

/**
 * This is the extension for javagen configuration.
 */
abstract class SchemaToJava(val name: String) {

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Inject
    abstract val layout: ProjectLayout

    private val encodingProperty = objectFactory.property(String::class.java)
    private val strictValidationProperty = objectFactory.property(Boolean::class.java)
    private val extensionProperty = objectFactory.property(Boolean::class.java)
    private val headerProperty = objectFactory.property(Boolean::class.java)
    private val packageNameProperty = objectFactory.property(String::class.java)
    private val targetVersionProperty = objectFactory.property(String::class.java)
    private val languageProperty = objectFactory.property(String::class.java)

    private val argsProperty = objectFactory.listProperty(String::class.java)

    private val schemaProperty = objectFactory.fileProperty()
    private val bindingProperty = objectFactory.fileProperty()
    private val catalogProperty = objectFactory.fileProperty()

    private val schemasProperty = objectFactory.fileCollection()
    private val bindingsProperty = objectFactory.fileCollection()

    private val outputDirProperty = objectFactory.directoryProperty()

    private val sourceSetNameProperty = objectFactory.property(String::class.java)
    private val antTaskClassNameProperty = objectFactory.property(String::class.java)

    /**
     * Provider for encoding property.
     */
    val encodingProvider: Provider<String>
        get() = encodingProperty

    /**
     * Parameter encoding for javagen.
     *
     * @property encoding
     */
    var encoding by encodingProperty

    /**
     * Provider for strictValidation property.
     */
    val strictValidationProvider: Provider<Boolean>
        get() = strictValidationProperty

    /**
     * Parameter strictValidation for javagen.
     *
     * @property strictValidation
     */
    var strictValidation by strictValidationProperty

    /**
     * Provider for extension property.
     */
    val extensionProvider: Provider<Boolean>
        get() = extensionProperty

    /**
     * Parameter extension for javagen.
     *
     * @property extension
     */
    var extension by extensionProperty

    /**
     * Provider for header property.
     */
    val headerProvider: Provider<Boolean>
        get() = headerProperty

    /**
     * Parameter header for javagen.
     *
     * @property header
     */
    var header by headerProperty

    /**
     * Provider for packageName property.
     */
    val packageNameProvider: Provider<String>
        get() = packageNameProperty

    /**
     * Parameter packageName for javagen.
     *
     * @property packageName
     */
    var packageName by packageNameProperty

    /**
     * Provider for targetVersion property.
     */
    val targetVersionProvider: Provider<String>
        get() = targetVersionProperty

    /**
     * Parameter targetVersion for javagen.
     *
     * @property targetVersion
     */
    var targetVersion by targetVersionProperty

    /**
     * Provider for language property.
     */
    val languageProvider: Provider<String>
        get() = languageProperty

    /**
     * Parameter language for javagen.
     *
     * @property language
     */
    var language by languageProperty

    /**
     * Provider for additional arguments.
     */
    val argsProvider: Provider<MutableList<String>>
        get() = argsProperty

    /**
     * List of additional arguments.
     *
     * @property args
     */
    var args by argsProperty

    /**
     * Add an argument to the list
     * of arguments.
     *
     * @param arg
     */
    fun arg(arg: String) {
        argsProperty.add(arg)
    }

    /**
     * Add a list of additional arguments to the list.
     *
     * @param args
     */
    fun args(args: List<String>) {
        argsProperty.addAll(args)
    }

    /**
     * Provider for schema file.
     */
    val schemaProvider: Provider<RegularFile>
        get() = schemaProperty

    /**
     * Schema file configuration for javagen.
     *
     * @property schema file for schema configuration
     */
    var schema: File?
            get()  {
                if(schemaProperty.orNull != null) {
                    return schemaProperty.get().asFile
                } else {
                    return null
                }
            }
            set(value) = schemaProperty.set(value)

    /**
     * Provider for binding file.
     */
    val bindingProvider: Provider<RegularFile>
        get() = bindingProperty

    /**
     * Binding file configuration for javagen.
     *
     * @property binding file for binding configuration
     */
    var binding: File?
        get()  {
            if(bindingProperty.orNull != null) {
                return bindingProperty.get().asFile
            } else {
                return null
            }
        }
        set(value) = bindingProperty.set(value)

    /**
     * Provider for catalog file.
     */
    val catalogProvider: Provider<RegularFile>
        get() = catalogProperty

    /**
     * Catalog file configuration for javagen.
     *
     * @property catalog file for catalog configuration
     */
    var catalog: File?
        get()  {
            if(catalogProperty.orNull != null) {
                return catalogProperty.get().asFile
            } else {
                return null
            }
        }
        set(value) = catalogProperty.set(value)

    /**
     * Schema file collection for javagen.
     *
     * @property schemas file collection with schema configuration
     */
    var schemas: FileCollection
        get() = schemasProperty
        set(value) = schemasProperty.setFrom(value)

    /**
     * Bindings file collection for javagen.
     *
     * @property bindings file collection with binding configuration
     */
    var bindings: FileCollection
        get() = bindingsProperty
        set(value) = bindingsProperty.setFrom(value)

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
     * Provider for used sourceSetName.
     */
    val sourceSetNameProvider: Provider<String>
        get() = sourceSetNameProperty

    /**
     * Parameter sourceSetName for Gradle JavaGen task.
     *
     * @property sourceSetName
     */
    var sourceSetName by sourceSetNameProperty

    /**
     * Provider for used  JavaGen antTaskClassName.
     */
    val antTaskClassNameProvider: Provider<String>
        get() = antTaskClassNameProperty

    /**
     * Parameter antTaskClassName for Gradle JavaGen task.
     *
     * @property antTaskClassName
     */
    var antTaskClassName by antTaskClassNameProperty

    init {
        encodingProperty.convention("UTF-8")
        strictValidationProperty.convention(true)
        extensionProperty.convention(false)
        headerProperty.convention(true)
        targetVersionProperty.convention("2.2")

        argsProperty.convention(listOf<String>())
        languageProperty.convention("XMLSCHEMA")

        val outPath = "${JaxbExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${JaxbExtension.JAXB_JAVAGEN_OUTPUTPATH}"
        outputDirProperty.convention(layout.getBuildDirectory().
                dir("${outPath}/${name.replace(' ', '_')}").get())

        sourceSetNameProperty.convention(JaxbExtension.DEFAULT_SOURCESET_NAME)
        antTaskClassNameProperty.convention(JaxbExtension.DEFAULT_XJC_TASK_CLASS_NAME)
    }

    /**
     * Calculates the task name.
     *
     * @property taskName name with prefix jaxbJavaGen
     */
    val taskName = "jaxbJavaGen" + name.capitalize()
}
