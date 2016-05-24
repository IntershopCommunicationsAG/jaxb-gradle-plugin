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

import org.gradle.api.Named
import org.gradle.api.file.FileCollection
import org.gradle.util.GUtil

/**
 * Java to schema extension
 * This is the configuration for schema generation.
 */
class JavaToSchema implements Named {

    JavaToSchema() {
        this.name = "schemaGenConfig"
    }

    JavaToSchema(String name) {
        this.name = name
    }

    String name

    /**
     * Java files are the base for the generation
     */
    FileCollection javaFiles

    /**
     * A map of name space configurations
     */
    Map<String, String> namespaceconfigs

    /**
     * Special parameters see schemagen documentation
     */
    String episode

    /**
     * Output directory
     */
    File outputDir

    /**
     * Calculates the task name
     *
     * @return task name with prefix jaxbSchemaGen
     */
    String getTaskName() {
        return "jaxbSchemaGen" + GUtil.toCamelCase(name);
    }
}
