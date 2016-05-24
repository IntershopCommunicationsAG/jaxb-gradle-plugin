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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * This extension provides the container for all jaxb related configurations.
 */
class JaxbExtension {

    /**
     * Default version of JAXB
     */
    final static String XJC_DEFAULT_VERSION = '2.2.11'

    /**
     * Extension name
     */
    final static String JAXB_EXTENSION_NAME = 'jaxb'
    /**
     * Dependency configuration name
     */
    final static String JAXB_CONFIGURATION_NAME = 'jaxb'
    /**
     * Task group name
     */
    final static String JAXB_TASK_GROUP = 'jaxb code generation'

    /**
     * Default output path
     */
    final static String CODEGEN_DEFAULT_OUTPUTPATH = 'generated/jaxb'

    /**
     * Folder name for generated code
     */
    final static String JAXB_JAVAGEN_OUTPUTPATH = 'java'
    final static String JAXB_SCHEMAGEN_OUTPUTPATH = 'schema'

    /**
     * Default source set name
     */
    final static String DEFAULT_SOURCESET_NAME = 'main'

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
    public JaxbExtension(Project project) {

        this.project = project

        if(! xjcVersion) {
            xjcVersion = XJC_DEFAULT_VERSION
        }

        schemaGen = project.container(JavaToSchema)
        javaGen = project.container(SchemaToJava)
    }

    /**
     * Version of xjc, default is 2.2.11
     */
    String xjcVersion

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
