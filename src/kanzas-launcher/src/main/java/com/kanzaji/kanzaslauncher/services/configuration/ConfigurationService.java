/**************************************************************************************
 * MIT License                                                                        *
 *                                                                                    *
 * Copyright (c) 2023-2024. Kanzaji                                                   *
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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.kanzaji.kanzaslauncher.services.IService;
import com.kanzaji.kanzaslauncher.services.LoggerCustom;
import com.kanzaji.kanzaslauncher.services.ServiceManager;
import com.kanzaji.kanzaslauncher.services.ServiceManager.State;
import org.jetbrains.annotations.NotNull;

public class ConfigurationService implements IService {
    private final LoggerCustom logger;
    private final String name;
    public final Path configFile;
    private final Map<String, ConfigurationKey> keys = new HashMap<>();
    private final Map<String, String> arguments = new HashMap<>();
    private boolean initialized = false;

    public ConfigurationService(String name, Path configFile) {
        this.name = name;
        this.configFile = configFile;
        this.logger = new LoggerCustom(name);
        ConfigurationConflictService.addService(this);
    }

    public void registerKey(@NotNull ConfigurationKey key) {
        if (ServiceManager.getStatus() != State.NOT_INIT) {
            throw new IllegalStateException("Registration of new Keys has to be done before PRE_INIT!");
        }
        if (keys.containsKey(Objects.requireNonNull(key, "Can't register null key!").getName())) {
            throw new IllegalArgumentException("A key is already registered under specified name!");
        }
        keys.put(key.getName(), key);
        logger.log("Registered configuration key under name: " + key.getName());
    }

    /**
     * Returns if ConfigurationService finished its initialization.
     * @return True if after INIT, otherwise false.
     */
    public boolean initialized() {
        return this.initialized;
    }

    /**
     * Returns description of the key.
     * @param key Name of the key to get description for.
     * @return The Description of the key. Can be null.
     */
    public String getDesc(String key) {
        return keys.get(Objects.requireNonNull(key)).getDesc();
    }

    /**
     * Used to get a map of registered configuration keys.
     * @return A copy of the map used to hold registered keys.
     */
    public Map<String, ConfigurationKey> getKeys() {
        return new HashMap<>(this.keys);
    }

    /**
     * @return The name of the service.
     */
    @Override
    public String getName() {
        return name;
    }

    public Object getValue(String key) {
        if (!this.initialized) throw new IllegalArgumentException(this.getName() + " is not yet initialized! Can't get config values before INIT.");
        if (!keys.containsKey(Objects.requireNonNull(key))) throw new IllegalArgumentException("Key with name: " + key + " not found!");
        return keys.get(key).getValue();
    }

    /**
     * Used to get lists of implemented phases.
     * Only phases mentioned in a returned list are executed, even if implemented.
     * @return List with implemented initialization phases.
     */
    @Override
    public List<State> getPhases() {
        return List.of(State.INIT, State.PRE_INIT, State.POST_INIT);
    }

    @Override
    public void preInit() {
        logger.log("PRE_INIT of " + getName() + " started. Gathering default values of the keys...");
        keys.forEach((name, value) -> {
            if (value.getValue() != value.getDefault()) {
                logger.warn("Default value CHANGED for key: " + name + "!");
                value.setValue(value.getDefault());
            }
        });
        //TODO: Config file generation
        logger.log("Finished PRE_INIT of " + getName() + ".");
    }

    @Override
    public void init() {
        //TODO: Config File validation and parsing.
        this.initialized = true;
    }
}
