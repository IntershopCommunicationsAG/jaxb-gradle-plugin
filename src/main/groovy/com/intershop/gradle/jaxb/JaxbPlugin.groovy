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
package com.intershop.gradle.jaxb

import com.intershop.gradle.jaxb.extension.JavaToSchema
import com.intershop.gradle.jaxb.extension.JaxbExtension
import com.intershop.gradle.jaxb.extension.SchemaToJava
import com.intershop.gradle.jaxb.task.JavaToSchemaTask
import com.intershop.gradle.jaxb.task.SchemaToJavaTask
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * Plugin implementation
 */
@CompileStatic
class JaxbPlugin implements Plugin<Project> {

    private JaxbExtension extension

    /**
     * Applies the extension and calls the
     * task initialization for this plugin
     *
     * @param project
     */
    void apply (Project project) {
        project.logger.info('Create extension {} for {}', JaxbExtension.JAXB_EXTENSION_NAME, project.name)
        extension = project.extensions.findByType(JaxbExtension) ?:  project.extensions.create(JaxbExtension.JAXB_EXTENSION_NAME, JaxbExtension, project)

        Configuration configuration = getConfiguration(project)

        Task jaxbTask = project.tasks.findByName('jaxb')
        if(! jaxbTask) {
            jaxbTask = project.getTasks().create('jaxb')
            jaxbTask.group = JaxbExtension.JAXB_TASK_GROUP
            jaxbTask.description = 'JAXB code generation tasks'
        }

        configureJavaCodeGenTasks(project, configuration, jaxbTask)
        configureSchemaCodeGenTasks(project, configuration, jaxbTask)
    }

    /**
     * Configures tasks for java code generation.
     *
     * @param project
     * @param jaxbTask
     */
    private void configureJavaCodeGenTasks(Project project, Configuration configuration, Task jaxbTask) {
        extension.getJavaGen().all {SchemaToJava javaGen ->
            SchemaToJavaTask task = project.getTasks().create(javaGen.getTaskName(), SchemaToJavaTask)
            task.group = JaxbExtension.JAXB_TASK_GROUP

            task.setOutputDir(javaGen.getOutputDirProvider())
            task.setJaxbConfiguration(configuration)

            task.setSchema(javaGen.getSchemaProvider())
            task.setBinding(javaGen.getBindingProvider())
            task.setCatalog(javaGen.getCatalogProvider())
            task.setSchemas(javaGen.getSchemas())
            task.setBindings(javaGen.getBindings())
            task.setPackageName(javaGen.getPackageNameProvider())
            task.setStrictValidation(javaGen.getStrictValidationProvider())
            task.setHeader(javaGen.getHeaderProvider())
            task.setTargetVersion(javaGen.getTargetVersionProvider())
            task.setEncoding(javaGen.getEncodingProvider())
            task.setExtension(javaGen.getExtensionProvider())
            task.setLanguage(javaGen.getLanguageProvider())
            task.setParameters(javaGen.getArgumentsProvider())
            task.setAntTaskClassName(javaGen.getAntTaskClassNameProvider())

            // identify sourceset configuration and add output to sourceset
            project.afterEvaluate {
                if (javaGen.getSourceSetName() && project.plugins.hasPlugin(JavaBasePlugin) && ! project.convention.getPlugin(JavaPluginConvention.class).sourceSets.isEmpty()) {
                    SourceSet sourceSet = project.convention.getPlugin(JavaPluginConvention.class).sourceSets.findByName(javaGen.getSourceSetName())
                    if(sourceSet != null) {
                        if(! sourceSet.java.srcDirs.contains(task.getOutputDir())) {
                            sourceSet.java.srcDir(task.getOutputDir())
                        }
                        project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn(task)
                    }
                }
            }

            // add task dependency for main task
            jaxbTask.dependsOn task
        }
    }

    /**
     * Configures tasks for schema generation.
     *
     * @param project
     * @param jaxbTask
     */
    private void configureSchemaCodeGenTasks(Project project, Configuration configuration, Task jaxbTask) {
        extension.getSchemaGen().all {JavaToSchema schemaGen ->
            JavaToSchemaTask task = project.getTasks().create(schemaGen.getTaskName(), JavaToSchemaTask)
            task.group = JaxbExtension.JAXB_TASK_GROUP

            task.setOutputDir(schemaGen.getOutputDirProvider())
            task.setInputDir(schemaGen.getInputDirProvider())
            task.setIncludes(schemaGen.getIncludesProvider())
            task.setExcludes(schemaGen.getExcludesProvider())

            task.setJaxbConfiguration(configuration)

            task.setNamespaceConfigs(schemaGen.getNamespaceconfigsProvider())
            task.setEpisode(schemaGen.getEpisodeProvider())

            // add task dependency for main task
            jaxbTask.dependsOn task
        }

    }

    /**
     * Adds the dependencies for the code generation. It is possible to override this.
     *
     * @param project
     * @param extension
     */
    private Configuration getConfiguration(final Project project) {
        final Configuration configuration =
                project.getConfigurations().findByName(JaxbExtension.JAXB_CONFIGURATION_NAME) ?:
                project.getConfigurations().create(JaxbExtension.JAXB_CONFIGURATION_NAME)

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("Jaxb configuration is used for code generation")
                .defaultDependencies(new Action<DependencySet>() {
            @Override
            void execute(DependencySet dependencies ) {
                // this will be executed if configuration is empty
                DependencyHandler dependencyHandler = project.getDependencies()

                dependencies.add(dependencyHandler.create('com.sun.xml.bind:jaxb-xjc:' + extension.getXjcVersion()))
                dependencies.add(dependencyHandler.create('com.sun.xml.bind:jaxb-impl:' + extension.getXjcVersion()))

                dependencies.add(dependencyHandler.create('com.sun.xml.bind:jaxb-jxc:' + extension.getXjcVersion()))
                dependencies.add(dependencyHandler.create('com.sun.xml.bind:jaxb-core:' + extension.getXjcVersion()))
            }
        })
        return configuration
    }
}
