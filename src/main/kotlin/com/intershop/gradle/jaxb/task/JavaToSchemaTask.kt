package com.intershop.gradle.jaxb.task

import com.intershop.gradle.jaxb.extension.JaxbExtension
import com.intershop.gradle.jaxb.extension.JaxbExtension.Companion.DEFAULT_SCHEMAGEN_TASK_CLASS_NAME
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
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
    var episode: String
        get() = episodeProperty.get()
        set(value) = episodeProperty.set(value)

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
    var antTaskClassName: String
        get() = antTaskClassNameProperty.get()
        set(value) = antTaskClassNameProperty.set(value)

    /**
     * This function set the classname of javagen.
     */
    fun provideAntTaskClassName(antTaskClassName: Provider<String>) =
            antTaskClassNameProperty.set(antTaskClassName)

    @get:InputFiles
    val jaxbClasspathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName(JaxbExtension.JAXB_CONFIGURATION_NAME))
        returnFiles
    }

    @get:InputFiles
    val classpathfiles : FileCollection by lazy {
        val returnFiles = project.files()
        // find files of original JASPER and Eclipse compiler
        returnFiles.from(project.configurations.findByName("compile"))
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
            argMap.put("episode", episode)
        }

        if(project.logger.isInfoEnabled) {
            project.logger.info("Arguments for schema: {}", argMap)
        }

        val classpath = classpathfiles + jaxbClasspathfiles

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
    }
}
