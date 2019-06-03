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
package com.intershop.gradle.jaxb.task

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

/**
 * Task for Java code generation
 */
@CompileStatic
@Slf4j
class JavaToSchemaTask extends DefaultTask {

    private Configuration jaxbConfiguration

    final DirectoryProperty outputDir = project.objects.directoryProperty()

    @OutputDirectory
    Directory getOutputDir() {
        return outputDir.get()
    }

    void setOutputDir(File outputDir) {
        this.outputDir.set(outputDir)
    }

    void setOutputDir(Provider<Directory> outputDir) {
        this.outputDir.set(outputDir)
    }

    final DirectoryProperty inputDir = project.objects.directoryProperty()

    @InputDirectory
    Directory getInputDir() {
        return inputDir.get()
    }

    void setInputDir(File inputDir) {
        this.inputDir.set(inputDir)
    }

    void setInputDir(Provider<Directory> inputDir) {
        this.inputDir.set(inputDir)
    }

    final ListProperty<String> excludes = project.objects.listProperty(String)

    @Input
    List<String> getExcludes() {
        return excludes.get()
    }

    void setExcludes(List<String> excludes) {
        this.excludes.set(excludes)
    }

    void setExcludes(Provider<List<String>> excludes) {
        this.excludes.set(excludes)
    }

    final ListProperty<String> includes = project.objects.listProperty(String)

    @Input
    List<String> getIncludes() {
        return includes.get()
    }

    void setIncludes(List<String> includes) {
        this.includes.set(includes)
    }

    void setIncludes(Provider<List<String>> includes) {
        this.includes.set(includes)
    }

    @Classpath
    Configuration getJaxbConfiguration() {
        return jaxbConfiguration
    }

    void setJaxbConfiguration(Configuration jaxbConfiguration ) {
        this.jaxbConfiguration = jaxbConfiguration
    }

    @Classpath
    Configuration getClasspathConfiguration() {
        return getProject().getConfigurations().maybeCreate('compile')
    }

    final MapProperty<String, String> namespaceConfigs = project.objects.mapProperty(String, String)

    @Optional
    @Input
    Map getNamespaceConfigs() {
        namespaceConfigs.getOrNull()
    }

    void setNamespaceConfigs(Map namespaceConfigs) {
        this.namespaceConfigs.set(namespaceConfigs)
    }

    void setNamespaceConfigs(Provider<Map<String, String>> namespaceConfigs) {
        this.namespaceConfigs.set(namespaceConfigs)
    }

    final Property<String> episode = project.objects.property(String)

    @Optional
    @Input
    String getEpisode() {
        episode.getOrElse('')
    }

    void setEpisode(String episode) {
        this.episode.set(episode)
    }

    void setEpisode(Provider<String> episode) {
        this.episode.set(episode)
    }

    private FileCollection sources = project.files()


    @CompileStatic(TypeCheckingMode.SKIP)
    @TaskAction
    void generate() {

        ant.taskdef(name: 'schemagen',
                classname: 'com.sun.tools.jxc.SchemaGenTask',
                classpath: getJaxbConfiguration().asPath)

        def args = [destdir	          : getOutputDir().asFile,
                    srcdir            : getInputDir().asFile,
                    includeantruntime : 'false'
        ]
        if(getEpisode()) {
            args << [episode : getEpisode()]
        }

        if(log.isInfoEnabled()) {
            log.info('Arguments for schema: {}', args)
        }

        FileCollection classpath = getClasspathConfiguration() + getJaxbConfiguration()

        ant.schemagen(args) {
            if(getNamespaceConfigs() && ! getNamespaceConfigs().isEmpty()) {
                getNamespaceConfigs().each { namespace, fileName ->
                    schema(namespace: namespace, file: fileName)
                }
            }
            if(! getExcludes().isEmpty()) {
                getExcludes().each {
                    exclude(name: it)
                }
            }
            if(! getIncludes().isEmpty()) {
                getIncludes().each {
                    include(name: it)
                }
            }
            classpath.addToAntBuilder(ant, 'classpath', FileCollection.AntType.ResourceCollection)
        }
    }


}
