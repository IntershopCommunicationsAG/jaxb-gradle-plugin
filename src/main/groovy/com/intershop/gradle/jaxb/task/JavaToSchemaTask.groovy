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
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Task for Java code generation
 */
@Slf4j
class JavaToSchemaTask extends DefaultTask {

    @OutputDirectory
    File outputDirectory

    @InputFiles
    ConfigurableFileTree sources

    @Optional
    @Input
    Map<String,String> namespaceConfigs

    @Optional
    @Input
    String episode

    @TaskAction
    void generate() {
        FileCollection jaxbConfiguration = project.configurations.getAt(JaxbExtension.JAXB_CONFIGURATION_NAME)

        ant.taskdef(name: 'schemagen',
                    classname: 'com.sun.tools.jxc.SchemaGenTask',
                    classpath: jaxbConfiguration.asPath)

        def args = [destdir	 : getOutputDirectory(),
                    srcdir   : getSources().getDir().absolutePath,
                    ]
        if(episode) {
            args << [episode : getEpisode()]
        }

        if(log.isInfoEnabled()) {
            log.info('Arguments for schema: {}', args)
        }

        FileCollection classpath = project.configurations.getByName('compile') + project.buildscript.configurations.getByName('classpath') + jaxbConfiguration

        ant.schemagen(args) {
            getNamespaceConfigs().each { namespace, fileName ->
                schema(namespace: namespace, file: fileName)
            }
            getSources().excludes.each {
                exclude(name: it)
            }
            getSources().includes.each {
                include(name: it)
            }
            classpath.addToAntBuilder(ant, 'classpath', FileCollection.AntType.ResourceCollection)
        }
    }
}
