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
package com.intershop.gradle.jaxb.task

import com.intershop.gradle.jaxb.extension.JaxbExtension
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

/**
 * Task for Java code generation
 */
@CompileStatic
@Slf4j
class SchemaToJavaTask extends DefaultTask {

    final Property<File> outputDir = project.objects.property(File)

    @OutputDirectory
    File getOutputDir() {
        return outputDir.get()
    }

    void setOutputDir(File outputDir) {
        this.outputDir.set(outputDir)
    }

    void setOutputDir(Provider<File> outputDir) {
        this.outputDir.set(outputDir)
    }

    final Property<File> schema = project.objects.property(File)

    @Optional
    @InputFile
    File getSchema() {
        schema.getOrNull()
    }

    void setSchema(File schema) {
        this.schema.set(schema)
    }

    void setSchema(Provider<File> schema) {
        this.schema.set(schema)
    }

    final Property<File> binding = project.objects.property(File)

    @Optional
    @InputFile
    File getBinding() {
        binding.getOrNull()
    }

    void setBinding(File binding) {
        this.binding.set(binding)
    }

    void setBinding(Provider<File> binding) {
        this.binding.set(binding)
    }

    final Property<File> catalog = project.objects.property(File)

    @Optional
    @InputFile
    File getCatalog() {
        catalog.getOrNull()
    }

    void setCatalog(File catalog) {
        this.catalog.set(catalog)
    }

    void setCatalog(Provider<File> catalog) {
        this.catalog.set(catalog)
    }

    final Property<FileCollection> schemas = project.objects.property(FileCollection)

    @Optional
    @InputFiles
    FileCollection getSchemas() {
        schemas.getOrNull()
    }

    void setSchemas(FileCollection schemas) {
        this.schemas.set(schemas)
    }

    void setSchemas(Provider<FileCollection> schemas) {
        this.schemas.set(schemas)
    }

    final Property<FileCollection> bindings = project.objects.property(FileCollection)

    @Optional
    @InputFiles
    FileCollection getBindings() {
        bindings.getOrNull()
    }

    void setBindings(FileCollection bindings) {
        this.bindings.set(bindings)
    }

    void setBindings(Provider<FileCollection> bindings) {
        this.bindings.set(bindings)
    }

    final Property<String> packageName = project.objects.property(String)

    @Optional
    @Input
    String getPackageName() {
        packageName.getOrNull()
    }

    void setPackageName(String packageName) {
        this.packageName.set(packageName)
    }

    void setPackageName(Provider<String> packageName) {
        this.packageName.set(packageName)
    }

    final Property<Boolean> strictValidation = project.objects.property(Boolean)

    @Optional
    @Input
    boolean getStrictValidation() {
        return strictValidation.get()
    }

    void setStrictValidation(boolean strictValidation) {
        this.strictValidation.set(strictValidation)
    }

    void setStrictValidation(Provider<Boolean> strictValidation) {
        this.strictValidation.set(strictValidation)
    }

    final Property<String> targetVersion = project.objects.property(String)

    @Optional
    @Input
    String getTargetVersion() {
        return targetVersion.get()
    }

    void setTargetVersion(String targetVersion) {
        this.targetVersion.set(targetVersion)
    }

    void setTargetVersion(Provider<String> targetVersion) {
        this.targetVersion.set(targetVersion)
    }

    final Property<String> encoding = project.objects.property(String)

    @Optional
    @Input
    String getEncoding() {
        return encoding.get()
    }

    void setEncoding(String encoding) {
        this.encoding.set(encoding)
    }

    void setEncoding(Provider<String> encoding) {
        this.encoding.set(encoding)
    }

    final Property<Boolean> header = project.objects.property(Boolean)

    @Optional
    @Input
    boolean getHeader() {
        return header.get()
    }

    void setHeader(boolean header) {
        this.header.set(header)
    }

    void setHeader(Provider<Boolean> header) {
        this.header.set(header)
    }

    final Property<Boolean> extension = project.objects.property(Boolean)

    @Optional
    @Input
    boolean getExtension() {
        return extension.get()
    }

    void setExtension(boolean extension) {
        this.extension.set(extension)
    }

    void setExtension(Provider<Boolean> extension) {
        this.extension.set(extension)
    }

    final Property<String> language = project.objects.property(String)

    @Optional
    @Input
    String getLanguage() {
        return language.get()
    }

    void setLanguage(String language) {
        this.language.set(language)
    }

    void setLanguage(Provider<String> language) {
        this.language.set(language)
    }

    final ListProperty<String> parameters = project.objects.listProperty(String)

    @Optional
    @Input
    List<String> getParameters() {
        return parameters.get()
    }

    void setParameters(List<String> parameters) {
        this.parameters.set(parameters)
    }

    void setParameters(Provider<List<String>> parameters) {
        this.parameters.set(parameters)
    }

    final Property<String> antTaskClassName = project.objects.property(String)

    @Input
    String getAntTaskClassName() {
        return antTaskClassName.get()
    }

    void setAntTaskClassName(String antTaskClassName) {
        this.antTaskClassName.set(antTaskClassName)
    }

    void setAntTaskClassName(Provider<String> antTaskClassName) {
        this.antTaskClassName.set(antTaskClassName)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @TaskAction
    void generate() {
        FileCollection jaxbConfiguration = getProject().getConfigurations().getAt(JaxbExtension.JAXB_CONFIGURATION_NAME)
        FileCollection xjcConfiguration = getProject().getConfigurations().maybeCreate('xjc')

        ant.taskdef (name : 'xjc',
                classname : getAntTaskClassName(),
                classpath : jaxbConfiguration.asPath)

        def args = [destdir	        : getOutputDir(),
                    language        : getLanguage(),
                    encoding        : getEncoding(),
                    header	        : getHeader(),
                    target          : getTargetVersion(),
                    extension       : getExtension()]

        if(getPackageName() != null) {
            args << [package: getPackageName()]
        }
        if(getSchema()) {
            args << [schema     : getSchema().absolutePath]
        }
        if(getBinding()) {
            args << [binding    : getBinding().absolutePath]
        }
        if(getCatalog()) {
            args << [catalog    : getCatalog().absolutePath]
        }

        if(log.isInfoEnabled()) {
            log.info('Arguments for xjc: {}', args)
        }


        synchronized (SchemaToJavaTask.class) {
            log.info(' -> Locked XJC Gradle Task to prevent the parallel execution!')
            ant.xjc(args) {
                if (xjcConfiguration) {
                    xjcConfiguration.addToAntBuilder(ant, 'classpath', FileCollection.AntType.ResourceCollection)
                }
                if (getSchemas()) {
                    getSchemas().addToAntBuilder(ant, 'schema', FileCollection.AntType.FileSet)
                }
                if (getBindings()) {
                    getBindings().addToAntBuilder(ant, 'binding', FileCollection.AntType.FileSet)
                }
                if (getParameters()) {
                    getParameters()?.each {
                        arg(value: it)
                    }
                }
                if(! getStrictValidation()) {
                    arg(value: '-nv')
                }
                if (log.isDebugEnabled()) {
                    arg(value: '-debug')
                }
                if (log.isInfoEnabled()) {
                    arg(value: '-verbose')
                }
            }
            log.info(' -> Unlocked XJC Gradle Task!')
        }
    }
}
