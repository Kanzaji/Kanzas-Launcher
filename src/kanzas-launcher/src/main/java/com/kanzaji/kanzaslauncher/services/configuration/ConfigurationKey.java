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

import com.kanzaji.kanzaslauncher.services.configuration.parsers.ThrowingFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
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
    private final Function<Object, Boolean> verifier;
    private final ThrowingFunction<Object, Object> parser;
    private Object value;

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param parser Not Required for some classes or keys for services without configuration files. Used to process Strings from Config file.
     * @param verifier Not Required. Used to verify if parsed value is acceptable.
     * @param argument Not Required. Used to add possibility to change value with use of the application arguments.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable ThrowingFunction<Object, Object> parser,
            @Nullable Function<Object, Boolean> verifier,
            @Nullable String argument,
            @Nullable String description
    ) {
        this.registry = registryName;
        this.defaultValue = defaultValue;
        this.value = defaultValue.get();
        this.valClass = value.getClass();

        this.parser = parser;
        this.verifier = verifier;

        this.argument = argument;
        this.desc = description;

        if (!valClass.isInstance(value)) throw new IllegalArgumentException("Default value is primitive or something really bad happened!");
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param parser Not Required for some classes or keys for services without configuration files. Used to process Strings from Config file.
     * @param argument Not Required. Used to add possibility to change value with use of the application arguments.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable ThrowingFunction<Object, Object> parser,
            @Nullable String argument,
            @Nullable String description
    ) {
        this(registryName, defaultValue, parser, null, argument, description);
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param parser Not Required for some classes or keys for services without configuration files. Used to process Strings from Config file.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable ThrowingFunction<Object, Object> parser,
            @Nullable String description
    ) {
        this(registryName, defaultValue, parser, null, null, description);
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param argument Not Required. Used to add possibility to change value with use of the application arguments.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable String argument,
            @Nullable String description
    ) {
        this(registryName, defaultValue, null, null, argument, description);
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable String description
    ) {
        this(registryName, defaultValue, null, null, null, description);
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Required. Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     * @param description Not Required. Used to add description in the configuration file of that key.
     */
    public ConfigurationKey(
            @NotNull String registryName,
            @NotNull Supplier<Object> defaultValue,
            @Nullable ThrowingFunction<Object, Object> parser
    ) {
        this(registryName, defaultValue, parser, null, null, null);
    }

    /**
     * Constructor of the ConfigurationKey.
     * @param registryName Used to represent the configuration key in the registry. Used as name in the Configuration file.
     * @param defaultValue Required non-native. Sets the class of this Configuration Key.
     */
    public ConfigurationKey(@NotNull String registryName, @NotNull Supplier<Object> defaultValue) {
        this(registryName, defaultValue, null, null, null, null);
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
     * @return Class of the value stored in this key.
     */
    public Class<?> getValueClass() {
        return this.valClass;
    }

    /**
     * Used to verify if passed value is acceptable for this key. Returns true if verifier is null.
     * @param value Object to verify.
     * @return True if object passes verification or verifier is null.
     */
    public Boolean verify(Object value) {
        return Objects.isNull(this.verifier) || this.verifier.apply(value);
    }

    /**
     * Used to parse data from Strings. If Parser is not present, returns original String.
     * @param value String with data to parse.
     * @return Result of the parser, or passed String if Parser not present.
     */
    public Object parse(Object value) throws Throwable {
        if (Objects.isNull(parser)) {
            return value;
        }
        return parser.apply(value);
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

    /**
     * Used to parse and set the value from String. Returns set value.
     * @param value String with data to parse and set.
     * @return Set value.
     */
    public Object parseAndSet(Object value) throws Throwable {
        return this.setValue(this.parse(value));
    }
}
