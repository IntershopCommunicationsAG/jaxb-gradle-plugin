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
 * Schema to extension
 * This is the configuration for java generation.
 */
class SchemaToJava implements Named {

    SchemaToJava() {
        this.name = "javaGenConfig"
    }

    SchemaToJava(String name) {
        this.name = name
    }

    String name

    /**
     * Encoding configuration
     */
    String encoding

    String getEncoding() {
        if(! this.encoding) {
            return 'UTF-8'
        } else {
            this.encoding
        }
    }

    /**
     * Special configuration, see xjc configuration
     */
    boolean strictValidation = true

    /**
     * Special configuration, see xjc configuration
     */
    boolean extension = false

    /**
     * Special configuration, see xjc configuration
     */
    boolean header = true

    /**
     * Package name of the generated code
     */
    String packageName

    /**
     * Single schema file
     */
    File schema

    /**
     * Single binding file
     */
    File binding

    /**
     * Single catalog file
     */
    File catalog

    /**
     * Schema files
     */
    FileCollection schemas

    /**
     * Binding files
     */
    FileCollection bindings

    /**
     * Output path
     */
    File outputDir

    /**
     * Target version for code generatation, see xjc configuration
     * default value is 2.2
     */
    String targetVersion

    String getTargetVersion() {
        if(! this.targetVersion) {
            return '2.2'
        } else {
            return this.targetVersion
        }
    }

    /**
     * Language version for code generatation, see xjc configuration
     * default value is XMLSCHEMA
     */
    String language

    String getLanguage() {
        if(! this.language) {
            return 'XMLSCHEMA'
        } else {
            return this.language
        }
    }

    /**
     * Name of the source set for generated Java code
     * default value is 'main'
     */
    String sourceSetName

    String getSourceSetName() {
        if(! this.sourceSetName) {
            return JaxbExtension.DEFAULT_SOURCESET_NAME
        } else {
            return this.sourceSetName
        }
    }

    /**
     * Additional ars for xjc
     */
    def args = []

    void arg(String parameter) {
        args.add(parameter)
    }

    /**
     * Calculates the task name
     *
     * @return task name with prefix jaxbJavaGen
     */
    String getTaskName() {
        return "jaxbJavaGen" + GUtil.toCamelCase(name);
    }
}
