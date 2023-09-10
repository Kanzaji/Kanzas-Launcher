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

package com.kanzaji.kanzasLauncher;

import com.kanzaji.kanzasLauncher.loggers.LoggerCustom;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CommandInterpreter {
    private static class instanceHolder {private static final CommandInterpreter instance = new CommandInterpreter();}
    private CommandInterpreter() {};
    private static final LoggerCustom logger = new LoggerCustom("CommandInterpreter");
    private final HashMap<String, Consumer<String>> registeredArguments = new HashMap<>();
    private final HashMap<String, Consumer<String>> registeredCommands = new HashMap<>();

    /**
     * Used to get instance of the CommandInterpreter object.
     * @return Instance of CommandInterpreter.
     */
    public static CommandInterpreter getInstance() {return instanceHolder.instance;}

    public void register(@NotNull String command, @NotNull Consumer<String> commandHandler) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(command);
        Objects.requireNonNull(commandHandler);
        if (command.contains(" ")) {throw new IllegalArgumentException("Passed command (\"" + command + "\") contains illegal values!");}

        registeredCommands.put(command.toLowerCase(Locale.ROOT), commandHandler);
    }

    public void registerArgument(@NotNull String argument, @NotNull Consumer<String> argumentHandler) throws NullPointerException {
        Objects.requireNonNull(argument);
        Objects.requireNonNull(argumentHandler);
        if (argument.contains(" ")) {throw new IllegalArgumentException("Passed command (\"" + argument + "\") contains illegal values!");}

        argument = argument.startsWith("-") ?
            argument.toLowerCase(Locale.ROOT).replaceFirst("-", "") :
            argument.toLowerCase(Locale.ROOT);
        registeredArguments.put(argument, argumentHandler);
    }

    public String decode(String command) {
        return command;
    }

    public void decodeArguments(@NotNull String[] args) throws NullPointerException {
        Objects.requireNonNull(args);
        logger.log("Decoding arguments:");
        Arrays.stream(args).toList().forEach(logger::log);

        for (String fullArgument : args) {
            String[] splitArgument = fullArgument.split(":", 2);
            String argument = splitArgument[0].startsWith("-") ?
                    splitArgument[0].toLowerCase(Locale.ROOT).replaceFirst("-", "") :
                    splitArgument[0].toLowerCase(Locale.ROOT);
            String value = (splitArgument.length > 1)? splitArgument[1]: "";

            if (registeredArguments.containsKey(argument)) {
                try {
                    registeredArguments.get(argument).accept(value);
                } catch (Exception e) {
                    logger.logStackTrace("Exception thrown while decoding argument \"" + argument + "\" with value \"" + value + "\"!", e);
                }
            }
        }
    }
}
