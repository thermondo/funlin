package de.thermondo.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

/**
 * Used inside [FunlinPluginExtension] to add options easily with default values.
 * ex: factory.property { set(false) }
 */
internal inline fun <reified T> ObjectFactory.property(
    configuration: Property<T>.() -> Unit = {}
): Property<T> = property(T::class.java).apply(configuration)
