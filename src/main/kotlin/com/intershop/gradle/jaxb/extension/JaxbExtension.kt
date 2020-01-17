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

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * This extension provides the container for all jaxb related configurations.
 */
open class JaxbExtension @Inject constructor(objectFactory: ObjectFactory) {

    companion object {
        // Extension name
        const val JAXB_EXTENSION_NAME = "jaxb"
        // Dependency configuration name
        const val JAXB_CONFIGURATION_NAME = "jaxb"

        const val ADD_JAXB_CONFIGURATION_NAME = "jaxbext"

        // Task group name
        const val JAXB_TASK_GROUP = "jaxb code generation"
        // Default output path
        const val CODEGEN_DEFAULT_OUTPUTPATH = "generated/jaxb"

        // Folder names for generated code
        const val JAXB_JAVAGEN_OUTPUTPATH = "java"
        const val JAXB_SCHEMAGEN_OUTPUTPATH = "schema"

        // Default source set name
        const val DEFAULT_SOURCESET_NAME = "main"

        // Default Ant task class name.
        const val DEFAULT_XJC_TASK_CLASS_NAME = "com.sun.tools.xjc.XJCTask"
        const val DEFAULT_SCHEMAGEN_TASK_CLASS_NAME = "com.sun.tools.jxc.SchemaGenTask"
    }

    val schemaGen = objectFactory.domainObjectContainer(JavaToSchema::class.java)

    val javaGen = objectFactory.domainObjectContainer(SchemaToJava::class.java)

}
