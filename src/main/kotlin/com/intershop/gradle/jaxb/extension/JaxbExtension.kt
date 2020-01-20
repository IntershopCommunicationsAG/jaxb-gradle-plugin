/*
 * Copyright 2019 Intershop Communications AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.intershop.gradle.jaxb.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * This extension provides the container for all jaxb related configurations.
 */
open class JaxbExtension @Inject constructor(objectFactory: ObjectFactory) {

    companion object {
        /**
         * Extension name of plugin.
         */
        const val JAXB_EXTENSION_NAME = "jaxb"

        /**
         * Dependency configuration name for jaxb code generation.
         */
        const val JAXB_CONFIGURATION_NAME = "jaxb"

        /**
         * Additional dependency configuration name for jaxb code generation
         * to extend the existing dependencies.
         */
        const val ADD_JAXB_CONFIGURATION_NAME = "jaxbext"

        /**
         * Task group name of jaxb code generation.
         */
        const val JAXB_TASK_GROUP = "jaxb code generation"

        /**
         * Default output path of all generation tasks.
         **/
        const val CODEGEN_DEFAULT_OUTPUTPATH = "generated/jaxb"

        /**
         * Folder names for generated java code.
         **/
        const val JAXB_JAVAGEN_OUTPUTPATH = "java"

        /**
         * Folder names for generated schema files.
         **/
        const val JAXB_SCHEMAGEN_OUTPUTPATH = "schema"

        /**
         * Default source set name used for code generation.
         **/
        const val DEFAULT_SOURCESET_NAME = "main"

        /**
         * Default Ant task class name for Java code generation.
         **/
        const val DEFAULT_XJC_TASK_CLASS_NAME = "com.sun.tools.xjc.XJCTask"

        /**
         * Default Ant task class name for Schema code generation.
         **/
        const val DEFAULT_SCHEMAGEN_TASK_CLASS_NAME = "com.sun.tools.jxc.SchemaGenTask"
    }


    /**
     * Domain object container of configurations for Java code generation.
     */
    val schemaGen: NamedDomainObjectContainer<JavaToSchema> = objectFactory.domainObjectContainer(JavaToSchema::class.java)

    /**
     * Domain object container of configurations for Schema code generation.
     */
    val javaGen: NamedDomainObjectContainer<SchemaToJava> = objectFactory.domainObjectContainer(SchemaToJava::class.java)

}
