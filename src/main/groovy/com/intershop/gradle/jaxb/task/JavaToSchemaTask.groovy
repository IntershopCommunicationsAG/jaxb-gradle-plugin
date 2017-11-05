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
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*

/**
 * Task for Java code generation
 */
@CompileStatic
@Slf4j
class JavaToSchemaTask extends DefaultTask {

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

    final Property<FileCollection> sources = project.objects.property(FileCollection)

    @InputFiles
    FileCollection getSources() {
        return sources.get()
    }

    void setSources(FileCollection sources) {
        this.sources.set(sources)
    }

    void setSources(Provider<FileCollection> sources) {
        this.sources.set(sources)
    }

    final Property<Map<String, String>> namespaceConfigs = project.objects.property(Map)

    @Optional
    @Input
    Map<String,String> getNamespaceConfigs() {
        namespaceConfigs.getOrNull()
    }

    void setNamespaceConfigs(Map<String, String> namespaceConfigs) {
        this.namespaceConfigs.set(namespaceConfigs)
    }

    void setNamespaceConfigs(Provider<Map<String, String>> namespaceConfigs) {
        this.namespaceConfigs.set(namespaceConfigs)
    }

    final Property<String> episode = project.objects.property(String)

    @Optional
    @Input
    String getEpisode() {
        episode.getOrNull()
    }

    void setEpisode(String episode) {
        this.episode.set(episode)
    }

    void setEpisode(Provider<String> episode) {
        this.episode.set(episode)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @TaskAction
    void generate() {
        FileCollection jaxbConfiguration = project.configurations.getAt(JaxbExtension.JAXB_CONFIGURATION_NAME)

        ant.taskdef(name: 'schemagen',
                    classname: 'com.sun.tools.jxc.SchemaGenTask',
                    classpath: jaxbConfiguration.asPath)

        def args = [destdir	 : getOutputDir(),
                    srcdir   : getSources().getDir().absolutePath,
                    ]
        if(episode != null) {
            args << [episode : getEpisode()]
        }

        if(log.isInfoEnabled()) {
            log.info('Arguments for schema: {}', args)
        }

        FileCollection classpath = project.configurations.getByName('compile') + project.buildscript.configurations.getByName('classpath') + jaxbConfiguration

        ant.schemagen(args) {
            getNamespaceConfigs().each { namespace, fileName ->
                schema(namespace: namespace, file: fileName)
            }
            getSources().excludes.each {
                exclude(name: it)
            }
            getSources().includes.each {
                include(name: it)
            }
            classpath.addToAntBuilder(ant, 'classpath', FileCollection.AntType.ResourceCollection)
        }
    }
}
