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
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * This extension provides the container for all jaxb related configurations.
 */
@CompileStatic
class JaxbExtension {

    /**
     * Extension name
     */
    public final static String JAXB_EXTENSION_NAME = 'jaxb'
    /**
     * Dependency configuration name
     */
    public final static String JAXB_CONFIGURATION_NAME = 'jaxb'
    /**
     * Task group name
     */
    public final static String JAXB_TASK_GROUP = 'jaxb code generation'

    /**
     * Default output path
     */
    public final static String CODEGEN_DEFAULT_OUTPUTPATH = 'generated/jaxb'

    /**
     * Folder name for generated code
     */
    public final static String JAXB_JAVAGEN_OUTPUTPATH = 'java'
    public final static String JAXB_SCHEMAGEN_OUTPUTPATH = 'schema'

    /**
     * Default source set name
     */
    public final static String DEFAULT_SOURCESET_NAME = 'main'

    /**
     * Default Ant task class name.
     */
    public final static String DEFAULT_ANT_TASK_CLASS_NAME = 'com.sun.tools.xjc.XJCTask'

    /**
     * Container for schema generation configurations
     */
    final NamedDomainObjectContainer<JavaToSchema> schemaGen

    /**
     * Container for jave generation configurations
     */
    final NamedDomainObjectContainer<SchemaToJava> javaGen

    private Project project

    /**
     * Initialize the extension.
     *
     * @param project
     */
    JaxbExtension(Project project) {

        this.project = project

        schemaGen = project.container(JavaToSchema, new JavaToSchemaFactory(project))
        javaGen = project.container(SchemaToJava, new SchemaToJavaFactory(project))
    }

    /**
     * Closure with the configuration of schema generation configurations
     * @param closure with schema generation configurations
     */
    void schemaGen(Closure c) {
        schemaGen.configure(c)
    }

    /**
     * Closure with the configuration of java generation configurations
     * @param closure with java generation configurations
     */
    void javaGen(Closure c) {
        javaGen.configure(c)
    }
}
