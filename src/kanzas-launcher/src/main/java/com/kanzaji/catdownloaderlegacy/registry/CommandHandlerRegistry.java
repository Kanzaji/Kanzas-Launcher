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

package com.kanzaji.catdownloaderlegacy.registry;

import com.kanzaji.catdownloaderlegacy.loggers.LoggerCustom;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CommandHandlerRegistry implements IHandlerRegistry {
    private static final LoggerCustom logger = new LoggerCustom("CommandHandlerRegistry");
    private static class instanceHolder {private static final CommandHandlerRegistry instance = new CommandHandlerRegistry();}
    private CommandHandlerRegistry() {}
    private final HashMap<String, Consumer<String>> registeredElements = new HashMap<>();
    private final HashSet<String> startupCommandBlackList = new HashSet<>();

    /**
     * Used to get instance of the CommandHandlerRegistry object.
     * @return Instance of CommandHandlerRegistry.
     */
    public static CommandHandlerRegistry getInstance() {return instanceHolder.instance;}

    /**
     * Used to get startup blacklisted elements of Command Registry.
     * @return HashSet with elements blacklisted from the startup.
     * @apiNote This method is specific for this implementation of IHandlerRegistry, and it may not be available in other implementations.
     */
    public HashSet<String> getBlacklistedElements() {
        return this.startupCommandBlackList;
    }

    /**
     * Used to register new command handlers to the CommandHandlerRegistry.
     * Command names are Case-Sensitive and can <i>not</i> contain spaces.
     * Multiple arguments are required to be handled at the handler level.
     * @param element Command name.
     * @param handler Consumer used to handle the command, with String value as an input.
     * @throws NullPointerException when Command name or CommandHandler are null.
     * @throws IllegalArgumentException when Command name contains spaces.
     */
    @Override
    public void register(@NotNull String element, @NotNull Consumer<String> handler) {
        this.register(element, handler, true);
    }

    /**
     * Used to register new command handlers to the CommandHandlerRegistry.
     * Command names are Case-Sensitive and can <i>not</i> contain spaces.
     * Multiple arguments are required to be handled at the handler level.
     * @param element Command name.
     * @param handler Consumer used to handle the command, with String value as an input.
     * @param startupExecution If false, this command will not be possible to be executed at the startup of the app.
     * @throws NullPointerException when Command name or CommandHandler are null.
     * @throws IllegalArgumentException when Command name contains spaces.
     */
    public void register(@NotNull String element, @NotNull Consumer<String> handler, boolean startupExecution) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(element);
        Objects.requireNonNull(handler);
        if (element.contains(" ")) {throw new IllegalArgumentException("Passed command (\"" + element + "\") contains illegal values!");}

        registeredElements.put(element.toLowerCase(Locale.ROOT), handler);
        if (!startupExecution) startupCommandBlackList.add(element);

        logger.log("Successfully registered " + (startupExecution? "": "runtime-only ") + "command handler for: " + element);
    }

    /**
     * Used to decode a specified String that contains command + command value and execute handler registered for that command.
     * @param fullCommand String to decode.
     * @return Status Message. Can be printed directly to the console.
     */
    @Override
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

        if (registeredElements.containsKey(command)) {
            try {
                logger.log("Found specified command in registry, executing command handler...");
                registeredElements.get(command).accept(value);
            } catch (Exception e) {
                logger.logStackTrace("Exception thrown while decoding command \"" + command + "\" with value \"" + value + "\"!", e);
                return "Exception was thrown while executing current command! " + e + ".\nFor more details, check the log file at " + logger.getLogPath();
            }
//        } else if (ArgumentHandlerRegistry.getInstance().getRegisteredArguments().containsKey(command)) {
//            logger.warn("Specified command is an Argument! If this is not user input, use CommandHandlerRegistry#decodeArguments for running argument decoding.");
//            return "Argument commands can only be executed on the startup!";
        } else {
            logger.warn("No specified command found!");
            return "No specified command found!";
        }

        String msg = "Command execution finished. Execution took " + (float) (System.currentTimeMillis() - time) / 1000F + "s.";
        logger.log(msg);
        return msg;
    }

    /**
     * Used to get registered elements of this registry. Does not return associated handlers for each element.
     * @return HashSet with the elements of the registry.
     */
    @Override
    public HashSet<String> getRegisteredElements() {
        HashSet<String> elements = new HashSet<>();
        this.registeredElements.forEach((command, __) -> elements.add(command));
        return elements;
    }
}
