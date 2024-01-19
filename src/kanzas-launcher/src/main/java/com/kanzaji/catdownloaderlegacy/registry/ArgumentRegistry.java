/**************************************************************************************
 * MIT License                                                                        *
 *                                                                                    *
 * Copyright (c) 2023. Kanzaji                                                        *
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

package com.kanzaji.catdownloaderlegacy.registry;

import com.kanzaji.catdownloaderlegacy.loggers.LoggerCustom;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ArgumentRegistry implements IHandlerRegistry {
    private static final LoggerCustom logger = new LoggerCustom("Argument Registry");
    private static final CommandHandlerRegistry cmdReg = CommandHandlerRegistry.getInstance();
    private static final class InstanceHolder {private static final ArgumentRegistry instance = new ArgumentRegistry();}
    private ArgumentRegistry() {}
    private final ConfigurationData ARD = new ConfigurationData();
    private final HashMap<String, Consumer<String>> registeredElements = new HashMap<>();

    /**
     * Used to get a reference to {@link ArgumentRegistry} instance.
     *
     * @return ArgumentHandlerRegistry with reference to the single instance of it.
     */
    public static ArgumentRegistry getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Used to get reference to {@link ConfigurationData} storing registry values.
     * @return ConfigurationData used to store registry Values.
     */
    public ConfigurationData getData() {
        return this.ARD;
    }

    /**
     * Used to get {@code HashSet<String>} with currently registered elements.
     * Does *not* return references to the handlers itself.
     * @return new {@code HashSet<String>} with all registered elements of this registry.
     */
    @Override
    public HashSet<String> getRegisteredElements() {
        HashSet<String> elements = new HashSet<>();
        this.registeredElements.forEach((command, __) -> elements.add(command));
        return elements;
    }

    /**
     * Used to decode provided String and execute associated handler from the Registry.
     * @param lineToDecode String to decode.
     * @return This implementation of the IHandlerRegistry#decode(String) doesn't return anything.
     */
    @Override
    public String decode(@NotNull String lineToDecode) {
        decode(new String[] { lineToDecode });
        return null;
    }

    /**
     * Used to decode provided String Array and execute associated handlers from the Registry.
     * @param arguments String array with arguments to decode.
     * @apiNote {@link ArgumentRegistry#decode(String)} call this method directly.
     */
    private void decode(@NotNull String[] arguments) {
        Objects.requireNonNull(arguments);

        logger.log("Decoding arguments:");
        Arrays.stream(arguments).toList().forEach(logger::log);

        HashSet<String> commands = new HashSet<>();

        for (String fullArgument : arguments) {
            String[] splitArgument = fullArgument.split(":", 2);
            String argument = splitArgument[0].startsWith("-") ?
                    splitArgument[0].toLowerCase(Locale.ROOT).replaceFirst("-", "") :
                    splitArgument[0].toLowerCase(Locale.ROOT);
            String value = (splitArgument.length > 1)? splitArgument[1]: "";

            if (registeredElements.containsKey(argument)) {
                try {
                    registeredElements.get(argument).accept(value);
                } catch (Exception e) {
                    logger.logStackTrace("Exception thrown while decoding argument \"" + argument + "\" with value \"" + value + "\"!", e);
                }
            } else if (cmdReg.getRegisteredElements().contains(argument)) {
                if (cmdReg.getBlacklistedElements().contains(argument)) {
                    logger.warn("Startup command found, however this command is blacklisted from the startup execution.");
                }
                logger.log("Startup command found! It will be executed after full argument decoding.");
                commands.add(argument + " " + value);
            }
        }

        commands.forEach(cmdReg::decode);
    }

    /**
     * Used to register new argument handlers to the ArgumentHandlerRegistry.
     * Argument names are Case-inSensitive and can <i>not</i> contain spaces.
     * Multiple value arguments are required to be handled at the handler level.
     * @param argument Argument name.
     * @param argumentHandler Consumer used to handle the argument, with String value as an input.
     * @throws NullPointerException when Command name or CommandHandler are null.
     * @throws IllegalArgumentException when Command name contains spaces.
     */
    @Override
    public void register(@NotNull String argument, @NotNull Consumer<String> argumentHandler) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(argument);
        Objects.requireNonNull(argumentHandler);
        if (argument.contains(" ")) {throw new IllegalArgumentException("Passed argument (\"" + argument + "\") contains illegal values!");}

        argument = argument.startsWith("-") ?
                argument.toLowerCase(Locale.ROOT).replaceFirst("-", "") :
                argument.toLowerCase(Locale.ROOT);
        registeredElements.put(argument, argumentHandler);
        logger.log("Successfully registered argument handler for: " + argument);
    }
}
