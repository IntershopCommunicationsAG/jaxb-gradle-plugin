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
package com.intershop.gradle.jaxb.extension

import groovy.transform.CompileStatic
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.*
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.util.GUtil

/**
 * Schema to extension
 * This is the configuration for java generation.
 */
@CompileStatic
class SchemaToJava implements Named {

    private final Project project
    final String name

    /**
     * Encoding configuration
     * Default value is UTF-8
     */
    private final Property<String> encoding

    Provider<String> getEncodingProvider() {
        return encoding
    }

    String getEncoding() {
        return encoding.get()
    }

    void setEncoding(String encoding) {
        this.encoding.set(encoding)
    }

    /**
     * Special configuration, see xjc configuration
     * Default value is true
     */
    private final Property<Boolean> strictValidation

    Provider<Boolean> getStrictValidationProvider() {
        return strictValidation
    }

    boolean getStrictValidation() {
        return strictValidation.get()
    }

    void setStrictValidation(boolean strictValidation) {
        this.strictValidation.set(strictValidation)
    }

    /**
     * Special configuration, see xjc configuration
     * Default value is false
     */
    private final Property<Boolean> extension

    Provider<Boolean> getExtensionProvider() {
        return extension
    }

    boolean getExtension() {
        return extension.get()
    }

    void setExtension(boolean extension) {
        this.extension.set(extension)
    }

    /**
     * Special configuration, see xjc configuration
     * Default value is true
     */
    private final Property<Boolean> header

    Provider<Boolean> getHeaderProvider() {
        return header
    }

    boolean getHeader() {
        return header.get()
    }

    void setHeader(boolean header) {
        this.header.set(header)
    }

    /**
     * Package name of the generated code
     */
    private final Property<String> packageName

    Provider<String> getPackageNameProvider() {
        return packageName
    }

    String getPackageName() {
        return packageName.get()
    }

    void setPackageName(String packageName) {
        this.packageName.set(packageName)
    }

    /**
     * Single schema file
     */
    private final RegularFileProperty schema

    Provider<RegularFile> getSchemaProvider() {
        return schema
    }

    RegularFile getSchema() {
        return schema.get()
    }

    void setSchema(File schema) {
        this.schema.set(schema)
    }

    /**
     * Single binding file
     */
    private final RegularFileProperty binding

    Provider<RegularFile> getBindingProvider() {
        return binding
    }

    RegularFile getBinding() {
        return binding.get()
    }

    void setBinding(File binding) {
        this.binding.set(binding)
    }

    /**
     * Single catalog file
     */
    private final RegularFileProperty catalog

    Provider<RegularFile> getCatalogProvider() {
        return catalog
    }

    RegularFile getCatalog() {
        return catalog.get()
    }

    void setCatalog(File catalog) {
        this.catalog.set(catalog)
    }

    /**
     * Schema files
     */
    private final ConfigurableFileCollection schemas

    FileCollection getSchemas() {
        return schemas
    }

    void setSchemas(FileCollection schemas) {
        this.schemas.setFrom(schemas)
    }

    /**
     * Binding files
     */
    private final ConfigurableFileCollection bindings

    FileCollection getBindings() {
        return bindings
    }

    void setBindings(FileCollection bindings) {
        this.bindings.setFrom(bindings)
    }

    /**
     * Output path
     */
    private final DirectoryProperty outputDir

    Provider<Directory> getOutputDirProvider() {
        return outputDir
    }

    Directory getOutputDir() {
        return outputDir.get()
    }

    void setOutputDir(File outputDir) {
        this.outputDir.set(outputDir)
    }

    /**
     * Target version for code generatation, see xjc configuration
     * default value is 2.2
     */
    private final Property<String> targetVersion

    Provider<String> getTargetVersionProvider() {
        return targetVersion
    }

    String getTargetVersion() {
        return targetVersion.get()
    }

    void setTargetVersion(String targetVersion) {
        this.targetVersion.set(targetVersion)
    }

    /**
     * Language version for code generatation, see xjc configuration
     * default value is XMLSCHEMA
     */
    private final Property<String> language

    Provider<String> getLanguageProvider() {
        return language
    }

    String getLanguage() {
        return language.get()
    }

    void setLanguage(String language) {
        this.language.set(language)
    }

    /**
     * Name of the source set for generated Java code
     * default value is 'main' (JaxbExtension.DEFAULT_SOURCESET_NAME)
     */
    private final Property<String> sourceSetName

    Provider<String> getSourceSetNameProvider() {
        return sourceSetName
    }

    String getSourceSetName() {
        return sourceSetName.get()
    }

    void setSourceSetName(String sourceSetName) {
        this.sourceSetName.set(sourceSetName)
    }

    /**
     * Additional args for xjc
    */
    private final ListProperty<String> arguments

    Provider<List<String>> getArgumentsProvider() {
        return arguments
    }

    List<String> getArgs() {
        return arguments.get()
    }

    void setArgs(List<String> args) {
        arguments.set(args)
    }

    void arg(String arg) {
        arguments.add(arg)
    }

    void args(List<String> args) {
        for(String arg : args) {
            arguments.add(arg)
        }
    }

    /**
     * The class name of the Ant task backing the Gradle task.
     * Default value is JaxbExtension.DEFAULT_ANT_TASK_CLASS_NAME
     */
    private final Property<String> antTaskClassName

    Provider<String> getAntTaskClassNameProvider() {
        return antTaskClassName
    }

    String getAntTaskClassName() {
        return antTaskClassName.get()
    }

    void setAntTaskClassName(String antTaskClassName) {
        this.antTaskClassName.set(antTaskClassName)
    }

    SchemaToJava(Project project, String name) {
        this.project = project
        this.name = name

        encoding = project.objects.property(String)
        strictValidation = project.objects.property(Boolean)
        extension = project.objects.property(Boolean)
        header = project.objects.property(Boolean)
        packageName = project.objects.property(String)
        schema = project.layout.fileProperty()
        binding = project.layout.fileProperty()
        catalog = project.layout.fileProperty()
        schemas = project.files()
        bindings = project.files()
        outputDir = project.layout.directoryProperty()
        targetVersion = project.objects.property(String)
        language = project.objects.property(String)
        sourceSetName = project.objects.property(String)
        arguments = project.objects.listProperty(String)
        antTaskClassName = project.objects.property(String)

        setEncoding('UTF-8')

        setStrictValidation(true)
        setExtension(false)
        setHeader(true)
        setArgs([])

        outputDir.set(project.getLayout().getBuildDirectory().dir("${JaxbExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${JaxbExtension.JAXB_JAVAGEN_OUTPUTPATH}/${name.replace(' ', '_')}").get())

        setTargetVersion('2.2')
        setLanguage('XMLSCHEMA')
        setSourceSetName(JaxbExtension.DEFAULT_SOURCESET_NAME)

        setAntTaskClassName(JaxbExtension.DEFAULT_ANT_TASK_CLASS_NAME)
    }

    /**
     * Calculates the task name
     *
     * @return task name with prefix jaxbJavaGen
     */
    String getTaskName() {
        return "jaxbJavaGen" + GUtil.toCamelCase(name)
    }
}
