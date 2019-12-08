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

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import kotlin.reflect.KProperty


/**
 * Provides 'set' functional extension for the Property object.
 */
operator fun <T> Property<T>.setValue(receiver: Any?, property: KProperty<*>, value: T) = set(value)
/**
 * Provides 'get' functional extension for the Property object.
 */
operator fun <T> Property<T>.getValue(receiver: Any?, property: KProperty<*>): T = get()

/**
 * Provides 'set' functional extension for the SetProperty object.
 */
operator fun <T> SetProperty<T>.setValue(receiver: Any?, property: KProperty<*>, value: Set<T>) = set(value)
/**
 * Provides 'get' functional extension for the SetProperty object.
 */
operator fun <T> SetProperty<T>.getValue(receiver: Any?, property: KProperty<*>): Set<T> = get()

/**
 * Provides 'set' functional extension for the ListProperty object.
 */
operator fun <T> ListProperty<T>.setValue(receiver: Any?, property: KProperty<*>, value: List<T>) = set(value)
/**
 * Provides 'get' functional extension for the ListProperty object.
 */
operator fun <T> ListProperty<T>.getValue(receiver: Any?, property: KProperty<*>): List<T> = get()

/**
 * Provides functional extension for primitive objects.
 */
inline fun <reified T> ObjectFactory.property(): Property<T> = property(T::class.java)
