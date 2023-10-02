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

public class CommandRegistry {
    private static class instanceHolder {private static final CommandRegistry instance = new CommandRegistry();}
    private CommandRegistry() {}

    private static final LoggerCustom logger = new LoggerCustom("CommandRegistry");
    private final HashMap<String, Consumer<String>> registeredArguments = new HashMap<>();
    private final HashMap<String, Consumer<String>> registeredCommands = new HashMap<>();
    private final HashSet<String> startupCommandBlackList = new HashSet<>();

    /**
     * Used to get instance of the CommandRegistry object.
     * @return Instance of CommandRegistry.
     */
    public static CommandRegistry getInstance() {return instanceHolder.instance;}

    /**
     * Used to register new command handlers to the CommandRegistry.
     * Command names are Case-Sensitive and can <i>not</i> contain spaces.
     * Multiple arguments are required to be handled at the handler level.
     * @param command Command name.
     * @param commandHandler Consumer used to handle the command, with String value as an input.
     * @throws NullPointerException when Command name or CommandHandler are null.
     * @throws IllegalArgumentException when Command name contains spaces.
     */
    public void register(@NotNull String command, @NotNull Consumer<String> commandHandler, boolean startupExecution) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(command);
        Objects.requireNonNull(commandHandler);
        if (command.contains(" ")) {throw new IllegalArgumentException("Passed command (\"" + command + "\") contains illegal values!");}

        registeredCommands.put(command.toLowerCase(Locale.ROOT), commandHandler);
        if (!startupExecution) startupCommandBlackList.add(command);

        logger.log("Successfully registered " + (startupExecution? "": "runtime-only ") + "command handler for: " + command);
    }

    /**
     * Used to register new argument handlers to the CommandRegistry.
     * Command names are Case-Sensitive and can <i>not</i> contain spaces.
     * Multiple arguments are required to be handled at the handler level.
     * @param argument Command name.
     * @param argumentHandler Consumer used to handle the command, with String value as an input.
     * @throws NullPointerException when Command name or CommandHandler are null.
     * @throws IllegalArgumentException when Command name contains spaces.
     */
    public void registerArgument(@NotNull String argument, @NotNull Consumer<String> argumentHandler) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(argument);
        Objects.requireNonNull(argumentHandler);
        if (argument.contains(" ")) {throw new IllegalArgumentException("Passed argument (\"" + argument + "\") contains illegal values!");}

        argument = argument.startsWith("-") ?
            argument.toLowerCase(Locale.ROOT).replaceFirst("-", "") :
            argument.toLowerCase(Locale.ROOT);
        registeredArguments.put(argument, argumentHandler);
        logger.log("Successfully registered argument handler for: " + argument);
    }

    /**
     * Used to decode a specified String that contains command + command value and execute handler registered for that command.
     * @param fullCommand String to decode.
     * @return Status Message. Can be printed directly to the console.
     */
    public String decode(@NotNull String fullCommand) {
        long time = System.currentTimeMillis();
        logger.log("Decoding line \"" + fullCommand + "\"...");

        String[] splitCommand = fullCommand.split(" ", 2);
        String command = splitCommand[0].startsWith("-") ?
                splitCommand[0].toLowerCase(Locale.ROOT).replaceFirst("-", "") :
                splitCommand[0].toLowerCase(Locale.ROOT);
        String value = (splitCommand.length > 1)? splitCommand[1]: "";

        logger.log("Command specified: " + command);
        logger.log("Command value: " + value);

        if (registeredCommands.containsKey(command)) {
            try {
                logger.log("Found specified command in registry, executing command handler...");
                registeredArguments.get(command).accept(value);
            } catch (Exception e) {
                logger.logStackTrace("Exception thrown while decoding command \"" + command + "\" with value \"" + value + "\"!", e);
                return "Exception was thrown while executing current command! " + e + ".\nFor more details, check the log file at " + logger.getLogPath();
            }
        } else if (registeredArguments.containsKey(command)) {
            logger.warn("Specified command is an Argument! If this is not user input, use CommandRegistry#decodeArguments for running argument decoding.");
            return "Argument commands can only be executed on the startup!";
        } else {
            logger.warn("No specified command found!");
            return "No specified command found!";
        }

        String msg = "Command execution finished. Execution took " + (float) (System.currentTimeMillis() - time) / 1000F + "s.";
        logger.log(msg);
        return msg;
    }

    /**
     * Used to decode an Argument list and execute handlers for specified arguments. Supports execution of commands on startup.
     * @param args String array with app arguments to decode.
     * @throws NullPointerException when args is null.
     */
    public boolean decodeArguments(@NotNull String[] args) throws NullPointerException {
        Objects.requireNonNull(args);
        logger.log("Decoding arguments:");
        Arrays.stream(args).toList().forEach(logger::log);

        HashMap<String, String> commands = new HashMap<>();

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
                    return false;
                }
            } else if (registeredCommands.containsKey(argument)) {
                if (startupCommandBlackList.contains(argument)) {
                    logger.warn("Startup command found, however this command is blacklisted from the startup execution.");
                    continue;
                }
                logger.log("Startup command found! It will be executed after full argument decoding.");
                commands.put(argument, value);
            }
        }

        commands.forEach((command, value) -> this.decode(command + value));
        return true;
    }
}
