/**************************************************************************************
 * MIT License                                                                        *
 *                                                                                    *
 * Copyright (c) 2024. Kanzaji                                                        *
 *                                                                                    *
 * Permission is hereby granted, free of charge, to any person obtaining a copy       *
 * of this software and associated documentation files (the "Software"), to deal      *
 * in the Software without restriction, including without limitation the rights       *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell          *
 * copies of the Software, and to permit persons to whom the Software is              *
 * furnished to do so, subject to the following conditions:                           *
 *                                                                                    *
 * The above copyright notice and this permission notice shall be included in all     *
 * copies or substantial portions of the Software.                                    *
 *                                                                                    *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR         *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,           *
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE       *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER             *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,      *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE      *
 * SOFTWARE.                                                                          *
 **************************************************************************************/

package com.kanzaji.kanzaslauncher.services.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Configuration key is used to register new configuration keys to any of the {@link ConfigurationService}s.
 */
public class ConfigurationKey {
    private final String registry;
    private final String argument;
    private final String desc;
    private final Class<?> valClass;
    private final Supplier<Object> defaultValue;
    private Object value;

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param argument Not Required. Used to add possibility to change value with use of the application arguments.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(@NotNull String registryName, @NotNull Supplier<Object> defaultValue, @Nullable String argument, @Nullable String description) {
        this.registry = registryName;
        this.argument = argument;
        this.desc = description;
        this.value = defaultValue.get();
        this.defaultValue = defaultValue;
        this.valClass = value.getClass();
    }

    /**
     * @return The registry name of this Configuration key.
     */
    public String getName() {
        return registry;
    }

    /**
     * @return The argument representation of this Configuration key. Can be null.
     */
    public String getArg() {
        return argument;
    }

    /**
     * @return The Description of the Configuration key. Can be null.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Return value of this configuration key.
     * @return Object with the value of this key.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Used to generate default value of this configuration key.
     * @return Object returned by default value supplier.
     */
    public Object getDefault() {
        return this.defaultValue.get();
    }

    /**
     * Used to set the value of this configuration key. Requires to be the same Class as the default value.
     * @param value Object of the same class as the default value.
     * @return Set value.
     * @throws IllegalArgumentException if Class of the passed object is not the same as the default object.
     */
    public Object setValue(Object value) {
        if (!valClass.isInstance(value) && Objects.nonNull(value)) {
            throw new IllegalArgumentException("Can't change class of configuration key! Expected: " + valClass + ", got: " +  value.getClass());
        }
        this.value = value;
        return getValue();
    }
}
