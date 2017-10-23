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
package com.intershop.gradle.jaxb.extension

import groovy.transform.CompileStatic
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.PropertyState
import org.gradle.api.provider.Provider
import org.gradle.util.GUtil

/**
 * Schema to extension
 * This is the configuration for java generation.
 */
@CompileStatic
class SchemaToJava implements Named {

    final Project project
    String name

    /**
     * Encoding configuration
     * Default value is UTF-8
     */
    private final PropertyState<String> encoding

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
    private final PropertyState<Boolean> strictValidation

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
    private final PropertyState<Boolean> extension

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
    private final PropertyState<Boolean> header

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
    private final PropertyState<String> packageName

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
    private final PropertyState<File> schema

    Provider<File> getSchemaProvider() {
        return schema
    }

    File getSchema() {
        return schema.get()
    }

    void setSchema(File schema) {
        this.schema.set(schema)
    }

    /**
     * Single binding file
     */
    private final PropertyState<File> binding

    Provider<File> getBindingProvider() {
        return binding
    }

    File getBinding() {
        return binding.get()
    }

    void setBinding(File binding) {
        this.binding.set(binding)
    }

    /**
     * Single catalog file
     */
    private final PropertyState<File> catalog

    Provider<File> getCatalogProvider() {
        return catalog
    }

    File getCatalog() {
        return catalog.get()
    }

    void setCatalog(File catalog) {
        this.catalog.set(catalog)
    }

    /**
     * Schema files
     */
    private final PropertyState<FileCollection> schemas

    Provider<FileCollection> getSchemasProvider() {
        return schemas
    }

    FileCollection getSchemas() {
        return schemas.get()
    }

    void setSchemas(FileCollection schemas) {
        this.schemas.set(schemas)
    }

    /**
     * Binding files
     */
    private final PropertyState<FileCollection> bindings

    Provider<FileCollection> getBindingsProvider() {
        return bindings
    }

    FileCollection getBindings() {
        return bindings.get()
    }

    void setBindings(FileCollection bindings) {
        this.bindings.set(bindings)
    }

    /**
     * Output path
     */
    private final PropertyState<File> outputDir

    Provider<File> getOutputDirProvider() {
        return outputDir
    }

    File getOutputDir() {
        return outputDir.get()
    }

    void setOutputDir(File outputDir) {
        this.outputDir.set(outputDir)
    }

    /**
     * Target version for code generatation, see xjc configuration
     * default value is 2.2
     */
    private final PropertyState<String> targetVersion

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
    private final PropertyState<String> language

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
    private final PropertyState<String> sourceSetName

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
    private final PropertyState<List<String>> argsProvider

    Provider<List<String>> getArgsProvider() {
        return argsProvider
    }

    List<String> getArgs() {
        return argsProvider.get()
    }

    void setArgs(List<String> args) {
        this.argsProvider.set(args)
    }

    void args(String paramater) {
        argsProvider.get().add(paramater)
    }

    /**
     * The class name of the Ant task backing the Gradle task.
     * Default value is JaxbExtension.DEFAULT_ANT_TASK_CLASS_NAME
     */
    private final PropertyState<String> antTaskClassName

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

        encoding = project.property(String)
        strictValidation = project.property(Boolean)
        extension = project.property(Boolean)
        header = project.property(Boolean)
        packageName = project.property(String)
        schema = project.property(File)
        binding = project.property(File)
        catalog = project.property(File)
        schemas = project.property(FileCollection)
        bindings = project.property(FileCollection)
        outputDir = project.property(File)
        targetVersion = project.property(String)
        language = project.property(String)
        sourceSetName = project.property(String)
        argsProvider = project.property(List)
        antTaskClassName = project.property(String)

        setEncoding('UTF-8')

        setStrictValidation(true)
        setExtension(false)
        setHeader(true)
        setArgs([])

        setOutputDir(new File(project.getBuildDir(),
                "${JaxbExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${JaxbExtension.JAXB_JAVAGEN_OUTPUTPATH}/${name.replace(' ', '_')}"))

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
        return "jaxbJavaGen" + GUtil.toCamelCase(name);
    }
}
