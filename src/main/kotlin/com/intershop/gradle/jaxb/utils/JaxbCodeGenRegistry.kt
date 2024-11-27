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
package com.intershop.gradle.jaxb.utils

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

/**
 * Prevents interference issues between --configuration-cache, gradle parallelism and ant tasks.
 * See JaxbPlugin for maxParallelism of this BuildService.
 * To reproduce issues:
 *   - run this plugin on a project with multiple generation tasks
 *   - raise the maxParallelism of this BuildService to 2 or more
 *   - run gradle with --configuration-cache
 */
abstract class JaxbCodeGenRegistry: BuildService<BuildServiceParameters.None>
