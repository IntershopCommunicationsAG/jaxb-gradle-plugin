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
import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Task for Java code generation
 */
@Slf4j
class SchemaToJavaTask extends DefaultTask {

    @OutputDirectory
    File outputDirectory

    @Optional
    @InputFile
    File schemaFile

    @Optional
    @InputFile
    File bindingFile

    @Optional
    @InputFile
    File catalogFile

    @Optional
    @InputFiles
    FileCollection schemaFiles

    @Optional
    @InputFiles
    FileCollection bindingFiles

    @Optional
    @Input
    String packageName

    @Optional
    @Input
    boolean strictValidation

    @Optional
    @Input
    String targetVersion

    @Optional
    @Input
    String encoding

    @Optional
    @Input
    boolean header

    @Optional
    @Input
    boolean extension

    @Optional
    @Input
    String language

    @Optional
    @Input
    List<String> parameters

    @Input
    String antTaskClassName

    @TaskAction
    void generate() {
        FileCollection jaxbConfiguration = getProject().getConfigurations().getAt(JaxbExtension.JAXB_CONFIGURATION_NAME)
        FileCollection xjcConfiguration = getProject().getConfigurations().maybeCreate('xjc')

        ant.taskdef (name : 'xjc',
                classname : getAntTaskClassName(),
                classpath : jaxbConfiguration.asPath)

        def args = [destdir	        : getOutputDirectory(),
                    language        : getLanguage(),
                    encoding        : getEncoding(),
                    header	        : getHeader(),
                    target          : getTargetVersion(),
                    extension       : getExtension()]

        if(getPackageName() != null) {
            args << [package: getPackageName()]
        }
        if(getSchemaFile()) {
            args << [schema     : getSchemaFile().absolutePath]
        }
        if(getBindingFile()) {
            args << [binding    : getBindingFile().absolutePath]
        }
        if(getCatalogFile()) {
            args << [catalog    : getCatalogFile().absolutePath]
        }

        if(log.isInfoEnabled()) {
            log.info('Arguments for xjc: {}', args)
        }

        if(! getStrictValidation()) {
            getParameters() << '-nv'
        }

        synchronized (SchemaToJavaTask.class) {
            log.info(' -> Locked XJC Gradle Task to prevent the parallel execution!')
            ant.xjc(args) {
                if (xjcConfiguration) {
                    xjcConfiguration.addToAntBuilder(ant, 'classpath', FileCollection.AntType.ResourceCollection)
                }
                if (getSchemaFiles()) {
                    getSchemaFiles().addToAntBuilder(ant, 'schema', FileCollection.AntType.FileSet)
                }
                if (getBindingFiles()) {
                    getBindingFiles().addToAntBuilder(ant, 'binding', FileCollection.AntType.FileSet)
                }
                if (getParameters()) {
                    getParameters()?.each {
                        arg(value: it)
                    }
                }
                if (log.isDebugEnabled()) {
                    arg(value: '-debug')
                }
                if (log.isInfoEnabled()) {
                    arg(value: '-verbose')
                }
            }
            log.info(' -> Unlocked XJC Gradle Task!')
        }
    }
}
