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

import com.intershop.gradle.jaxb.extension.JaxbExtension
import com.intershop.gradle.test.AbstractProjectSpec
import org.gradle.api.Plugin

class JaxbPluginSpec extends AbstractProjectSpec {

    @Override
    Plugin getPlugin() {
        return new JaxbPlugin()
    }

    def 'should add extension named jaxb'() {
        when:
        plugin.apply(project)

        then:
        project.extensions.getByName(JaxbExtension.JAXB_EXTENSION_NAME)
    }

    def 'should add JAXB generate task for each jaxb config'() {
        when:
        plugin.apply(project)

        then:
        project.extensions.getByName(JaxbExtension.JAXB_EXTENSION_NAME).getXjcVersion() == JaxbExtension.XJC_DEFAULT_VERSION

        when:
        project.extensions.getByName(JaxbExtension.JAXB_EXTENSION_NAME).schemaGen {
            testconfiguration {
            }
        }

        then:
        project.tasks.findByName("jaxbSchemaGenTestconfiguration")

        when:
        project.extensions.getByName(JaxbExtension.JAXB_EXTENSION_NAME).javaGen {
            testconfiguration {
            }
        }

        then:
        project.tasks.findByName("jaxbJavaGenTestconfiguration")
    }
}
