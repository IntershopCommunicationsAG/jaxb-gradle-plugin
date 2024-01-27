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

import com.intershop.gradle.jaxb.JaxbPlugin.Companion.JAXB_REGISTRY
import com.intershop.gradle.jaxb.extension.JaxbExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceRegistry
import org.gradle.api.services.internal.BuildServiceProvider
import org.gradle.api.services.internal.BuildServiceRegistryInternal
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.CompileClasspath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.resources.ResourceLock
import org.gradle.kotlin.dsl.withGroovyBuilder
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * This task generates a java code from an
 * existing jaxb configuration.
 */
@CacheableTask
open class SchemaToJavaTask @Inject constructor(
        objectFactory: ObjectFactory): DefaultTask() {

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
    private val antTaskClassNameProperty = objectFactory.property(String::class.java)

    init {
        encodingProperty.convention("UTF-8")
        strictValidationProperty.convention(false)
        extensionProperty.convention(false)
        headerProperty.convention(false)

        encodingProperty.convention("UTF-8")
        targetVersionProperty.convention("")

        argsProperty.convention(listOf<String>())
        languageProperty.convention("XMLSCHEMA")
    }

    /**
     * Parameter encoding for javagen.
     *
     * @property encoding
     */
    @get:Input
    var encoding: String?
        get() = encodingProperty.orNull
        set(value) = encodingProperty.set(value)

    /**
     * This function set the input property encoding.
     */
    fun provideEncoding(encoding: Provider<String>) = encodingProperty.set(encoding)

    /**
     * Parameter strictValidation for javagen.
     *
     * @property strictValidation
     */
    @get:Input
    var strictValidation: Boolean
        get() = strictValidationProperty.get()
        set(value) = strictValidationProperty.set(value)

    /**
     * This function set the input property strictValidation.
     */
    fun provideStrictValidation(strictValidation: Provider<Boolean>) = strictValidationProperty.set(strictValidation)

    /**
     * Parameter extension for javagen.
     *
     * @property extension
     */
    @get:Input
    var extension: Boolean
        get() = extensionProperty.get()
        set(value) = extensionProperty.set(value)

    /**
     * This function set the input property extension.
     */
    fun provideExtension(extension: Provider<Boolean>) = extensionProperty.set(extension)

    /**
     * Parameter header for javagen.
     *
     * @property header
     */
    @get:Input
    var header: Boolean
        get() = headerProperty.get()
        set(value) = headerProperty.set(value)

    /**
     * This function set the input property header.
     */
    fun provideHeader(header: Provider<Boolean>) = headerProperty.set(header)

    /**
     * Parameter packageName for javagen.
     *
     * @property packageName
     */
    @get:Optional
    @get:Input
    var packageName: String?
        get() = packageNameProperty.orNull
        set(value) = packageNameProperty.set(value)

    /**
     * This function set the input property packageName.
     */
    fun providePackageName(packageName: Provider<String>) = packageNameProperty.set(packageName)

    /**
     * Parameter targetVersion for javagen.
     *
     * @property targetVersion
     */
    @get:Input
    var targetVersion: String
        get() = targetVersionProperty.get()
        set(value) = targetVersionProperty.set(value)

    /**
     * This function set the input property targetVersion.
     */
    fun provideTargetVersion(targetVersion: Provider<String>) = targetVersionProperty.set(targetVersion)

    /**
     * Parameter language for javagen.
     *
     * @property language
     */
    @get:Input
    var language: String
        get() = languageProperty.get()
        set(value) = languageProperty.set(value)

    /**
     * This function set the input property language.
     */
    fun provideLanguage(language: Provider<String>) = languageProperty.set(language)

    /**
     * List of additional arguments for javagen.
     *
     * @property args Provider<MutableList<String>>
     */
    @get:Input
    var args: MutableList<String>
        get() = argsProperty.get()
        set(value) = argsProperty.set(value)

    /**
     * This function set the input property of additional arguments.
     */
    fun provideArgs(args: Provider<MutableList<String>>) = argsProperty.set(args)

    /**
     * Schema file for java generation.
     *
     * @property schema
     */
    @get:Optional
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var schema: File?
        get() {
            return if(schemaProperty.isPresent) {
                schemaProperty.get().asFile
            } else {
                null
            }
        }
        set(value) = schemaProperty.set(value)

    /**
     * This function set the input property schema file.
     */
    fun provideSchema(schema: Provider<RegularFile>) = schemaProperty.set(schema)

    /**
     * Binding file for java generation.
     *
     * @property binding
     */
    @get:Optional
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var binding: File?
        get() {
            return if(bindingProperty.isPresent) {
                bindingProperty.get().asFile
            } else {
                null
            }
        }
        set(value) = bindingProperty.set(value)

    /**
     * This function set the input property schema file.
     */
    fun provideBinding(binding: Provider<RegularFile>) = bindingProperty.set(binding)

    /**
     * Catalog file for java generation.
     *
     * @property catalog
     */
    @get:Optional
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var catalog: File?
        get() {
            return if(catalogProperty.isPresent) {
                catalogProperty.get().asFile
            } else {
                null
            }
        }
        set(value) = catalogProperty.set(value)

    /**
     * This function set the input property catalog file.
     */
    fun provideCatalog(catalog: Provider<RegularFile>) = catalogProperty.set(catalog)

    /**
     * Schema files property for javagen.
     *
     * @property schemas
     */
    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var schemas: FileCollection
        get() = schemasProperty
        set(value) {
            schemasProperty.from(value)
        }

    /**
     * Schema files property for javagen.
     *
     * @property schemas
     */
    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var bindings: FileCollection
        get() = bindingsProperty
        set(value) {
            bindingsProperty.from(value)
        }

    /**
     * This is the classname of javagen.
     *
     * @property antTaskClassName
     */
    @get:Input
    var antTaskClassName: String
        get() = antTaskClassNameProperty.get()
        set(value) = antTaskClassNameProperty.set(value)

    /**
     * This function set the classname of javagen.
     */
    fun provideAntTaskClassName(antTaskClassName: Provider<String>) =
            antTaskClassNameProperty.set(antTaskClassName)

    /**
     * Classpath files for schema code generation (see Jaxb configuration (JAXB_CONFIGURATION_NAME)).
     *
     * @property jaxbClasspathfiles
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val jaxbClasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(JaxbExtension.JAXB_CONFIGURATION_NAME))
        returnFiles
    }

    /**
     * Additional classpath files for schema code generation (see Jaxb configuration (ADD_JAXB_CONFIGURATION_NAME)).
     *
     * @property addjaxbClasspathfiles
     */
    @get:Classpath
    val addjaxbClasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(JaxbExtension.ADD_JAXB_CONFIGURATION_NAME))
        returnFiles
    }

    /**
     * Classpath files for Schema generation.
     *
     * @property classpathfiles
     */
    @get:CompileClasspath
    val classpathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME))
        returnFiles
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

    @Internal
    override fun getSharedResources(): List<ResourceLock> {
        val locks = ArrayList(super.getSharedResources())
        val serviceRegistry = services.get(BuildServiceRegistryInternal::class.java)
        val jaxbResourceProvider = getBuildService(serviceRegistry)
        val resource = serviceRegistry.forService(jaxbResourceProvider)
        locks.add(resource?.resourceLock)

        return Collections.unmodifiableList(locks)
    }

    private fun getBuildService(registry: BuildServiceRegistry): BuildServiceProvider<out BuildService<*>,
            out BuildServiceParameters> {
        val registration = registry.registrations.findByName(JAXB_REGISTRY)
                ?: throw GradleException ("Unable to find build service with name '$name'.")

        return registration.getService() as BuildServiceProvider<*,*>
    }

    /**
     * This creates the java code fron a JAXB configuration.
     * It calls the original ant task with necessary parameters.
     */
    @TaskAction
    fun generate() {
        val argMap = mutableMapOf(
                "destdir" to outputDir.absolutePath,
                "language" to language,
                "encoding" to encoding,
                "header" to header.toString(),
                "extension" to extension.toString(),
                "fork" to true
        )
        if(targetVersion == "2.0" || targetVersion == "2.1") {
            argMap["target"] = targetVersion
        }

        if(packageName != null) {
            argMap["package"] = packageName
        }

        if(schemaProperty.isPresent) {
            argMap["schema"] = schemaProperty.get().asFile.absolutePath
        }
        if(bindingProperty.isPresent) {
            argMap["binding"] = bindingProperty.get().asFile.absolutePath
        }
        if(catalogProperty.isPresent) {
            argMap["catalog"] = catalogProperty.get().asFile.absolutePath
        }

        if(project.logger.isInfoEnabled) {
            project.logger.info("Argeument for javagen: {}", argMap)
        }

        val taskClassPath = jaxbClasspathfiles + addjaxbClasspathfiles

        project.logger.info(" -> Locked XJC Gradle Task to prevent the parallel execution!")

        System.setProperty("javax.xml.accessExternalSchema", "all")
        System.setProperty("javax.xml.accessExternalDTD", "all")

        ant.properties.putAll(
                mapOf( "javax.xml.accessExternalSchema" to "all",
                        "javax.xml.accessExternalDTD" to "file,http")
        )

        ant.withGroovyBuilder {
            "taskdef"(
                    "name" to "jaxb",
                    "classname" to antTaskClassName,
                    "classpath" to taskClassPath.asPath)



            "jaxb"(argMap) {
                "classpath" {
                    "pathelement"( "path" to classpathfiles.asPath)
                }


                if(! schemas.isEmpty) {
                    schemas.addToAntBuilder(ant, "schema", FileCollection.AntType.FileSet)
                }

                if(! bindings.isEmpty) {
                    bindings.addToAntBuilder(ant, "binding", FileCollection.AntType.FileSet)
                }

                "arg"("value" to "-disableXmlSecurity")

                args.forEach {
                    "arg"("value" to it)
                }

                if(! strictValidation) {
                    "arg"( "value" to "-nv")
                }
                if (project.logger.isInfoEnabled || project.logger.isDebugEnabled) {
                    "arg"( "value" to "-verbose")
                } else {
                    "arg"( "value" to "-quiet")
                }

                "jvmarg"("value" to "-DenableExternalEntityProcessing=true")

            }
        }
    }
}
