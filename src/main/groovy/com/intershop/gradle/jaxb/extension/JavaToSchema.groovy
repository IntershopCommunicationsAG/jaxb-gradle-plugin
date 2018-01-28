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
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.util.GUtil

/**
 * Java to schema extension
 * This is the configuration for schema generation.
 */
@CompileStatic
class JavaToSchema implements Named {

    private final Project project
    final String name
    
    /**
     * Input path
     */
    private final DirectoryProperty inputDir

    Provider<Directory> getInputDirProvider() {
        return inputDir
    }

    Directory getInputDir() {
        return inputDir.get()
    }

    void setInputDir(File inputDir) {
        this.inputDir.set(inputDir)
    }
    
    private final ListProperty<String> excludes
    
    Provider<List<String>> getExcludesProvider() {
        return excludes
    }
    
    List<String> getExcludes() {
        return excludes.get()
    }
    
    void setExcludes(List<String> excludes) {
        this.excludes.set(excludes)
    }

    void exclude(String exclude) {
        excludes.add(exclude)
    }

    private final ListProperty<String> includes
    
    Provider<List<String>> getIncludesProvider() {
        return includes
    }
    
    List<String> getIncludes() {
        return includes.get()
    }
    
    void setIncludes(List<String> includes) {
        this.includes.set(includes)
    }

    void include(String include) {
        includes.add(include)
    }

    /**
     * A map of name space configurations
     */
    private final Property<Map> namespaceconfigs

    Provider<Map> getNamespaceconfigsProvider() {
        return namespaceconfigs
    }

    Map<String, String> getNamespaceconfigs() {
        return namespaceconfigs.get()
    }

    void setNamespaceconfigs(Map namespaceconfigs) {
        this.namespaceconfigs.set(namespaceconfigs)
    }

    /**
     * Special parameters see schemagen documentation
     */
    private final Property<String> episode

    Provider<String> getEpisodeProvider() {
        return episode
    }

    String getEpisode() {
        return episode.get()
    }

    void setEpisode(String episode) {
        this.episode.set(episode)
    }

    /**
     * Special parameters for classpath calculation
     */
    private final ListProperty<String> configurationNames

    Provider<List<String>> getConfigurationNamesProvider() {
        return configurationNames
    }

    List<String> getConfigurationNames() {
        return configurationNames.get()
    }

    void setConfigurationNames(List<String> configurations) {
        configurationNames.set(configurations)
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

    JavaToSchema(Project project, String name) {
        this.project = project
        this.name = name

        inputDir = project.layout.directoryProperty()
        includes = project.objects.listProperty(String)
        excludes = project.objects.listProperty(String)

        namespaceconfigs = project.objects.property(Map)
        episode = project.objects.property(String)
        outputDir = project.layout.directoryProperty()

        configurationNames = project.objects.listProperty(String)

        outputDir.set(project.getLayout().getBuildDirectory().
                dir("${JaxbExtension.CODEGEN_DEFAULT_OUTPUTPATH}/${JaxbExtension.JAXB_SCHEMAGEN_OUTPUTPATH}/${name.replace(' ', '_')}").get())

        includes.add( '**/**/*.java' )

        configurationNames.set(['default'])
    }

    /**
     * Calculates the task name
     *
     * @return task name with prefix jaxbSchemaGen
     */
    String getTaskName() {
        return "jaxbSchemaGen" + GUtil.toCamelCase(name)
    }
}
