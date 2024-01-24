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

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kanzaji.kanzaslauncher.services.IService;
import com.kanzaji.kanzaslauncher.services.LoggerCustom;
import com.kanzaji.kanzaslauncher.services.ServiceManager;
import com.kanzaji.kanzaslauncher.services.ServiceManager.State;
import org.jetbrains.annotations.NotNull;

public class ConfigurationService implements IService {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final LoggerCustom logger;
    private final String name;
    public final Path configFile;
    private final Map<String, ConfigurationKey> keys = new LinkedHashMap<>();
    private final Map<String, String> arguments = new HashMap<>();
    private boolean initialized = false;

    /**
     * @param name Name of the service.
     * @param configFile Path to the configuration file.
     */
    public ConfigurationService(String name, Path configFile) {
        this.name = name;
        this.configFile = configFile;
        this.logger = new LoggerCustom(name);
        ConfigurationConflictService.addService(this);
    }

    /**
     * Used to create ConfigurationService without configuration file.
     * @param name Name of the service.
     */
    public ConfigurationService(String name) {
        this(name, null);
    }

    public void registerKey(@NotNull ConfigurationKey key) {
        if (ServiceManager.getStatus() != State.NOT_INIT) {
            throw new IllegalStateException("Registration of new Keys has to be done before PRE_INIT!");
        }
        if (keys.containsKey(Objects.requireNonNull(key, "Can't register null key!").getName())) {
            throw new IllegalArgumentException("A key is already registered under specified name!");
        }
        keys.put(key.getName(), key);
        if (key.getArg() != null) arguments.put(key.getArg(), key.getName());
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
        return new LinkedHashMap<>(this.keys);
    }

    /**
     * @return The name of the service.
     */
    @Override
    public String getName() {
        return name;
    }

    public Object getValue(@NotNull String key) {
        if (!this.initialized) throw new IllegalStateException(this.getName() + " is not yet initialized! Can't get config values before INIT.");
        if (!keys.containsKey(Objects.requireNonNull(key))) throw new IllegalArgumentException("Key with name: " + key + " not found!");
        return keys.get(key).getValue();
    }

    public Object setValue(@NotNull String key, @NotNull Object value) {
        if (!this.initialized) throw new IllegalStateException(this.getName() + " is not yet initialized! Can't get config values before INIT.");
        if (!keys.containsKey(Objects.requireNonNull(key))) throw new IllegalArgumentException("Key with name: " + key + " not found!");
        return keys.get(key).setValue(Objects.requireNonNull(value, "Value can't be null!"));
    }

    /**
     * Used to get lists of implemented phases.
     * Only phases mentioned in a returned list are executed, even if implemented.
     * @return List with implemented initialization phases.
     */
    @Override
    public List<State> getPhases() {
        return List.of(State.INIT, State.PRE_INIT);
    }

    @Override
    public void preInit() throws IOException {
        logger.log("Gathering default values of the keys...");
        keys.forEach((name, value) -> {
            if (value.getValue() != value.getDefault()) {
                logger.warn("Default value CHANGED for key: " + name + "!");
                value.setValue(value.getDefault());
            }
        });
        if (hasConfig() && !Files.exists(configFile)) generateConfig(configFile);
        logger.log("PRE_INIT Finished.");
    }

    @Override
    public void init() throws IOException {
        if (hasConfig() && !Files.exists(configFile)) throw new IllegalStateException("Configuration File was not generated in the PRE_INIT! Something isn't right.");

        if (!hasConfig()) {
            logger.log("No configuration file for this Configuration Service.");
            this.initialized = true;
            return;
        }

        logger.log("Loading configuration file...");
        Map<String, Object> config = gson.fromJson(Files.readString(configFile), new TypeToken<Map<String, Object>>(){}.getType());
        AtomicBoolean configRegeneration = new AtomicBoolean(false);

        keys.forEach((name, key) -> {
            if (!config.containsKey(name)) {
                logger.warn("Missing key from configuration file! " + name + " (" + key.getValueClass().getName() + ")");
                configRegeneration.set(true);
                return;
            }

            Object value = config.get(name);
            config.remove(name);

            if (!key.verify(value)) {
                logger.error("Illegal value for key: " + name + "! Value: " + value);
                configRegeneration.set(true);
                return;
            }

            try {
                key.parseAndSet(value);
            } catch (Throwable e) {
                logger.logStackTrace("Exception while parsing value for key: " + name, e);
                configRegeneration.set(true);
            }
        });

        if (!config.keySet().isEmpty()) {
            logger.warn("Additional keys found in the configuration file!");
            configRegeneration.set(true);
            config.forEach((name, value) -> logger.warn("- " + name + " -> " + value));
        }

        if (configRegeneration.get()) {
            logger.warn("Config \"" + configFile.toAbsolutePath() + "\" appears to be incorrect. Correcting...");
            Files.deleteIfExists(configFile);
            this.generateConfig(configFile);
        }

        logger.log("Configuration file loaded. INIT phase finished.");
        this.initialized = true;
    }

    /**
     * @return True if Service has Configuration file, otherwise false.
     */
    public boolean hasConfig() {
        return Objects.nonNull(this.configFile);
    }

    private void generateConfig(Path path) throws IOException {
        path = path.toAbsolutePath();
        if (Files.exists(path)) throw new FileAlreadyExistsException("Specified location already exists!");
        logger.log("Generating configuration file...");

        Map<String, Object> config = new LinkedHashMap<>();
        keys.forEach((name, key) -> config.put(name, key.getValue()));
        String[] configJsonArray = gson.toJson(config).split("\n");

        int index = 0;
        List<String> keyNames = keys.keySet().stream().toList();
        StringBuilder configJson = new StringBuilder();

        for (String entry : configJsonArray) {
            ConfigurationKey key = keys.get(keyNames.get(index));
            if (entry.strip().startsWith("\"" + key.getName() + "\":")) {
                if (index+1 < keyNames.size()) index++;
                if (key.getDesc() != null) configJson.append("  // ").append(key.getDesc()).append("\n");
                if (key.getArg() != null) configJson.append("  // Argument representation: -").append(key.getArg()).append("\n");
            }
            configJson.append(entry).append("\n\n");
        }

        // Removes additional \n on the start and the end of the Configuration json. Only cosmetic
        configJson.replace(0, 3, "{\n").replace(configJson.length()-4, configJson.length(), "}");

        Files.createDirectories(path.getParent());
        Files.createFile(path);
        Files.writeString(path, configJson.toString());
        logger.log("Saved configuration file to: \"" + path + "\".");
    }
}
