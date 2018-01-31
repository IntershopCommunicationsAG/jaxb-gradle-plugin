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
package com.intershop.gradle.jaxb;

import com.intershop.gradle.jaxb.extension.JaxbExtension;
import com.intershop.gradle.jaxb.task.JavaToSchemaTask;
import com.intershop.gradle.jaxb.task.SchemaToJavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.jetbrains.annotations.NotNull;

public class JaxbPlugin implements Plugin<Project> {

    private JaxbExtension extension;

    /**
     * Applies the extension and calls the
     * task initialization for this plugin
     *
     * @param project current project
     */
    public void apply (@NotNull Project project) {
        project.getLogger().info("Create extension {} for {}", JaxbExtension.JAXB_EXTENSION_NAME, project.getName());

        extension = project.getExtensions().findByType(JaxbExtension.class);
        if(extension == null)  {
            extension  = project.getExtensions().create(JaxbExtension.JAXB_EXTENSION_NAME, JaxbExtension.class, project);
        }

        Configuration configuration = getConfiguration(project);

        Task jaxbTask = project.getTasks().findByName("jaxb");
        if(jaxbTask == null) {
            jaxbTask = project.getTasks().create("jaxb");
            jaxbTask.setGroup(JaxbExtension.JAXB_TASK_GROUP);
            jaxbTask.setDescription("JAXB code generation tasks");
        }

        configureJavaCodeGenTasks(project, configuration, jaxbTask);
        configureSchemaCodeGenTasks(project, configuration, jaxbTask);
    }

    /**
     * Configures tasks for java code generation.
     *
     * @param project       current project
     * @param configuration configuration for jaxb dependencies
     * @param jaxbTask      the main jaxb task
     */
    private void configureJavaCodeGenTasks(Project project, Configuration configuration, Task jaxbTask) {
        extension.getJavaGen().all(schemaToJava ->
                project.getTasks().create(schemaToJava.getTaskName(), SchemaToJavaTask.class, schemaToJavaTask -> {
            schemaToJavaTask.setDescription("Generate java code for " + schemaToJava.getName());
            schemaToJavaTask.setGroup(JaxbExtension.JAXB_TASK_GROUP);

            schemaToJavaTask.setOutputDir(schemaToJava.getOutputDirProvider());
            schemaToJavaTask.setJaxbConfiguration(configuration);

            schemaToJavaTask.setSchema(schemaToJava.getSchemaProvider());
            schemaToJavaTask.setBinding(schemaToJava.getBindingProvider());
            schemaToJavaTask.setCatalog(schemaToJava.getCatalogProvider());
            schemaToJavaTask.setSchemas(schemaToJava.getSchemas());
            schemaToJavaTask.setBindings(schemaToJava.getBindings());
            schemaToJavaTask.setPackageName(schemaToJava.getPackageNameProvider());
            schemaToJavaTask.setStrictValidation(schemaToJava.getStrictValidationProvider());
            schemaToJavaTask.setHeader(schemaToJava.getHeaderProvider());
            schemaToJavaTask.setTargetVersion(schemaToJava.getTargetVersionProvider());
            schemaToJavaTask.setEncoding(schemaToJava.getEncodingProvider());
            schemaToJavaTask.setExtension(schemaToJava.getExtensionProvider());
            schemaToJavaTask.setLanguage(schemaToJava.getLanguageProvider());
            schemaToJavaTask.setParameters(schemaToJava.getArgumentsProvider());
            schemaToJavaTask.setAntTaskClassName(schemaToJava.getAntTaskClassNameProvider());

            project.getPlugins().withType(JavaBasePlugin.class, javaBasePlugin -> {
                JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
                SourceSet sourceSet = javaPluginConvention.getSourceSets().findByName(schemaToJava.getSourceSetName());
                if(sourceSet != null) {
                    Directory outputDir = schemaToJavaTask.getOutputDir();
                    if(! sourceSet.getJava().getSrcDirs().contains(outputDir.getAsFile())) {
                        sourceSet.getJava().srcDir(outputDir);
                    }
                    project.getTasks().getByName(sourceSet.getCompileJavaTaskName()).dependsOn(schemaToJavaTask);
                }
            });

            jaxbTask.dependsOn(schemaToJavaTask);
        }));
    }

    /**
     * Configures tasks for schema generation.
     *
     * @param project       current project
     * @param configuration configuration for jaxb dependencies
     * @param jaxbTask      the main jaxb task
     */
    private void configureSchemaCodeGenTasks(Project project, Configuration configuration, Task jaxbTask) {
        extension.getSchemaGen().all(javaToSchema ->
                project.getTasks().create(javaToSchema.getTaskName(), JavaToSchemaTask.class, javaToSchemaTask -> {
            javaToSchemaTask.setDescription("Generate Schema for " + javaToSchema.getName());
            javaToSchemaTask.setGroup(JaxbExtension.JAXB_TASK_GROUP);

            javaToSchemaTask.setOutputDir(javaToSchema.getOutputDirProvider());
            javaToSchemaTask.setInputDir(javaToSchema.getInputDirProvider());
            javaToSchemaTask.setIncludes(javaToSchema.getIncludesProvider());
            javaToSchemaTask.setExcludes(javaToSchema.getExcludesProvider());

            javaToSchemaTask.setJaxbConfiguration(configuration);

            javaToSchemaTask.setNamespaceConfigs(javaToSchema.getNamespaceconfigsProvider());
            javaToSchemaTask.setEpisode(javaToSchema.getEpisodeProvider());

            // add task dependency for main task
            jaxbTask.dependsOn(javaToSchemaTask);
        }));
    }

    /**
     * Adds the dependencies for the code generation. It is possible to override this.
     *
     * @param project   current project
     */
    private Configuration getConfiguration(final Project project) {
        Configuration configuration = project.getConfigurations().findByName(JaxbExtension.JAXB_CONFIGURATION_NAME);
        if(configuration == null) {
            configuration = project.getConfigurations().create(JaxbExtension.JAXB_CONFIGURATION_NAME);
        }

        configuration
                .setVisible(false)
                .setTransitive(false)
                .setDescription("Jaxb configuration is used for code generation")
                .defaultDependencies(dependencies -> {
                    // this will be executed if configuration is empty
                    DependencyHandler dependencyHandler = project.getDependencies();

                    dependencies.add(dependencyHandler.create("com.sun.xml.bind:jaxb-xjc:" + extension.getXjcVersion()));
                    dependencies.add(dependencyHandler.create("com.sun.xml.bind:jaxb-impl:" + extension.getXjcVersion()));

                    dependencies.add(dependencyHandler.create("com.sun.xml.bind:jaxb-jxc:" + extension.getXjcVersion()));
                    dependencies.add(dependencyHandler.create("com.sun.xml.bind:jaxb-core:" + extension.getXjcVersion()));
                });
        return configuration;
    }
}
