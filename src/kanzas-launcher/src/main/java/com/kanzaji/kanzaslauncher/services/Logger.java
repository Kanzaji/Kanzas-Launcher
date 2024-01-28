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

package com.kanzaji.kanzaslauncher.services;

import com.kanzaji.catdownloaderlegacy.registry.ArgumentHandlerRegistry;
import com.kanzaji.catdownloaderlegacy.utils.DateUtils;
import com.kanzaji.catdownloaderlegacy.utils.FileUtils;
import com.kanzaji.kanzaslauncher.services.interfaces.ILogger;
import com.kanzaji.kanzaslauncher.services.interfaces.IService;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class is the main instance of the Logger Service. It handles creation, stockpiling and logging to log files.
 * @apiNote This class is currently under a rework, this might cause issues and unexpected behavior until rework is complete.
 */
public class Logger implements ILogger, IService {
    //TODO:
    // - Rework POST_INIT
    // - Implement new ConfigurationService, remove use of old ARD from CDL
    @Deprecated(forRemoval = true, since = "0.0.1-DEV")
    private static final ArgumentHandlerRegistry ARD = ArgumentHandlerRegistry.getInstance();
    private static final class InstanceHolder {private static final Logger instance = new Logger();}
    private Logger() {}
    private boolean crashed = false;
    private boolean initialized = false;
    private Path LogFile = Path.of("Kanza's-Launcher.log");
    private final List<PreInitMessage> preInitMessages = new LinkedList<>();

    /**
     * Used to get an instance of the Logger.
     * @return Reference to an instance of the Logger.
     * @apiNote This is meant only for situations before ServiceManager is available,
     * please use {@link ServiceManager#get(String)} with {@link Services#LOGGER} instead.
     */
    public static Logger getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Used to get new ILogger instance wrapping this logger with custom name.
     * @param name name to use in the logs.
     * @return new ILogger implementation with name before all logged messages.
     */
    public ILogger get(String name) {
        return new ILogger() {
            private final Logger logger = Logger.getInstance();
            @Override
            public String getLogPath() {
                return logger.getLogPath();
            }

            @Override
            public boolean isInitialized() {
                return logger.isInitialized();
            }

            @Override
            public void logCustom(String msg, int type, @Nullable Throwable throwable) {
                logger.logCustom("[" + name + "] " + msg, type, throwable);
            }
        };
    }

    /**
     * Used to get lists of implemented phases.
     * Only phases mentioned in a returned list are executed, even if implemented.
     * @return List with implemented initialization phases.
     */
    @Override
    public List<ServiceManager.State> getPhases() {
        return List.of(ServiceManager.State.PRE_INIT, ServiceManager.State.POST_INIT);
    }

    /**
     * Used to get the name of the Service for Service Manager.
     * @return String with Service name.
     */
    @Override
    public String getName() {
        return "Logger Service";
    }

    /**
     * Used to get a path to a log file.
     * @return String with the absolute path of a log file.
     */
    public String getLogPath() {
        if (this.LogFile == null) return null;
        return this.LogFile.toAbsolutePath().toString();
    }

    /**
     * Used to initialize Logger. Creates new log file and overrides old one if present.
     * @apiNote This is separate from preInit of IService for future possibility of changes.
     */
    private void initLogger() throws Throwable {
        try {
            if (Files.exists(this.LogFile)) {
                Files.move(this.LogFile, Path.of("Kanza's-Launcher Archived.log"), StandardCopyOption.REPLACE_EXISTING);
                this.log("Old Log file found! \"" + this.LogFile.toAbsolutePath() + "\" file has been archived for now.");
            }
            Files.createFile(this.LogFile);
            this.log("\"" + this.LogFile.toAbsolutePath() + "\" file created.");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        this.log("Logger Initialization completed.");
        this.initialized = true;

        logPreInitMessages();
    }

    /**
     * Used as a last resort to try to initialize the logger. If it fails, it prints everything to the console.
     * @see Logger#initLogger() for any other scenario for logger initialization.
     */
    public void crashInit() {
        try {
            this.initLogger();
            this.warn("Logger was initialized with use of CRASH initialization!");
        } catch (Throwable ex) {
            this.initialized = true;
            if (!Files.exists(this.LogFile)) {
                try {
                    Files.createFile(this.LogFile);
                    this.warn("Successfully created new log file.");
                } catch (Exception e) {
                    this.error("Failed creating log file! Printing entire pre-init messages to the console.");
                    crashed = true;
                }
            } else {
                this.warn("USING OLD LOG FILE DUE TO APPLICATION CRASH.");
            }
            logPreInitMessages();
        }
    }

    private void logPreInitMessages() {
        if (!preInitMessages.isEmpty()) preInitMessages.forEach(msg -> this.logCustom(msg.msg, msg.type, msg.ex));
    }

    /**
     * Used to initialize Logger. Creates new log file and overrides old one if present.
     * @see Logger#initLogger()
     */
    @Override
    public void preInit() throws Throwable {
        this.initLogger();
    }

    public void newpostInit() {

    }

    /**
     * Used to finish initialization of the Logger.
     * Handles the Stockpiling function of the logs, and moving the log file to a new location.
     * @throws IllegalStateException when reading attributes of the compressed log files is not possible.
     * @throws IOException when IO Exception occurs.
     */
    @Override
    public void postInit() throws IllegalStateException, IOException {
        Path logPath = Path.of(ARD.getLogPath());
        Path archivedLog = Path.of("Kanza's-Launcher Archived.log");
        Path logInLogPath = Path.of(logPath.toString(), "Kanza's-Launcher.log");
        Path archivedLogInLogPath = Path.of(logPath.toString(), "Kanza's-Launcher Archived.log");

        // Move Log files to the log Path if specified.
        if (!FileUtils.getParentFolder(Path.of(logPath.toString(), ".")).toString().equals(FileUtils.getParentFolder(this.LogFile).toString())) {
            if (Files.notExists(logPath)) {
                this.log("Custom path for logs has been specified, but it doesn't exists! Creating \"" + logPath.toAbsolutePath() + "\".");
                Files.createDirectory(logPath);
            } else {
                this.log("Custom path for logs has been specified: \"" + logPath.toAbsolutePath() + "\".");
            }

            // Checking if non-fully archived log is present in the new log location.
            if (Files.exists(archivedLogInLogPath)) {
                this.warn("Found old pre-full-archive log file in specified Path! This might signal a crash in the last post-init phase of the logger!");
                this.warn("The log file is going to be saved as unknown.log" + (ARD.shouldCompressLogs()? ".gz": "") + " for future inspection.");
                 if (ARD.shouldCompressLogs()) {
                     FileUtils.compressToGz(archivedLogInLogPath,"unknown.log", true);
                 } else {
                     FileUtils.rename(archivedLogInLogPath, "unknown.log");
                 }
            }

            // Kanza's-Launcher Archived.log handling.
            if (Files.exists(archivedLog)) {
                if (ARD.shouldStockpileLogs()) {
                    this.log("Found archived log in working directory! Moving archived log to new location...");
                    Files.move(archivedLog, archivedLogInLogPath);
                    archivedLog = archivedLogInLogPath;
                    if (ARD.shouldCompressLogs()) {
                        this.log("Moved archived log to new location! Compressing...");
                        FileUtils.compressToGz(archivedLog, DateUtils.getCurrentFullDate() + ".log", true);
                    } else {
                        this.log("Moved archived log to new location! Changing the filename...");
                        FileUtils.rename(archivedLog, DateUtils.getCurrentFullDate() + ".log");
                    }
                    this.log("Old log file has been archived!");
                } else {
                    this.log("Found archived log in working directory! However, stockpiling of the logs has been disabled. Deleting old log file...");
                    Files.deleteIfExists(archivedLog);
                    this.log("Old log file has been deleted!");
                }
            }

            // Kanza's-Launcher.log handling.
            if (Files.exists(logInLogPath)) {
                if (ARD.shouldStockpileLogs()) {
                    this.log("Old log file found in the log Directory! Archiving the log file...");
                    String archivedName = FileUtils.rename(logInLogPath, "Kanza's-Launcher Archived.log");
                    Path archivedFile = Path.of(logPath.toString(), archivedName);
                    if (ARD.shouldCompressLogs()) {
                        FileUtils.compressToGz(archivedFile, DateUtils.getCurrentFullDate() + ".log", true);
                    } else {
                        FileUtils.rename(archivedFile, DateUtils.getCurrentFullDate() + ".log");
                    }
                    this.log("Old log file has been archived!");
                } else {
                    this.log("Found old log file in the Log directory! However, stockpiling of the logs has been disabled. Deleting old log file...");
                    Files.deleteIfExists(logInLogPath);
                    this.log("Old log file has been deleted!");
                }
            }

            // Currently Active log handling.
            if (Files.exists(this.LogFile)) {
                this.log("Moving currently active log file to new location...");
                if (Files.exists(logInLogPath)) {
                    this.error("Found non-archived log in the final destination, what should not happen at this point of the process!");
                    this.error("Archiving the log under unknown_latest.log" + (ARD.shouldCompressLogs()? ".gz": "") + "  name for future inspection.");
                    String unknownName = FileUtils.rename(logInLogPath, "unknown_latest.log");
                    FileUtils.compressToGz(Path.of(logPath.toString(), unknownName), true);
                }
                Files.move(this.LogFile, logInLogPath);
                this.LogFile = logInLogPath;
                this.log("Moved currently active log to the new Location: \"" + this.LogFile.toAbsolutePath() + "\".");
            } else {
                this.error("The log file doesn't exists before even archiving??? Something is horribly wrong...");
            }
        } else {
            this.log("No custom path for Logs has been specified, using working directory for logging!");
            if (Files.exists(archivedLog)) {
                if(ARD.shouldStockpileLogs()) {
                    this.log("Old log file found! Archiving the log file...");
                    if (ARD.shouldCompressLogs()) {
                        FileUtils.compressToGz(archivedLog, DateUtils.getCurrentFullDate() + ".log", true);
                    } else {
                        FileUtils.rename(archivedLog, DateUtils.getCurrentFullDate() + ".log");
                    }
                    this.log("Log has been archived!");
                } else {
                    this.log("Old log file found! However, stockpiling of the logs has been disabled. Deleting old log file...");
                    Files.delete(archivedLog);
                    this.log("Old log file has been deleted.");
                }
            }
        }

        // Limit handling
        if (ARD.shouldStockpileLogs() && ARD.getLogStockSize() > 0) {
            this.log("Stockpiling of the logs is enabled! Stockpile limit is " +  ARD.getLogStockSize());
            List<Path> archivedLogs = new LinkedList<>();
            try(Stream<Path> directoryList = Files.list(logPath)) {
                directoryList.forEach((File) -> {
                    String fileName = File.getFileName().toString();
                    if(fileName.contains(".log") && !fileName.equals("Kanza's-Launcher.log")) {
                        archivedLogs.add(File);
                    }
                });
            }

            if (archivedLogs.size() > ARD.getLogStockSize()) {
                this.log("Limit of stockpile has been reached (Currently found " + archivedLogs.size() + " log files)! Deleting the oldest files...");

                archivedLogs.sort((e1, e2) -> {
                    try { return Files
                            .readAttributes(e1, BasicFileAttributes.class)
                            .creationTime()
                            .compareTo(Files
                                .readAttributes(e2, BasicFileAttributes.class)
                                .creationTime()
                            );
                    } catch (Exception e) {
                        this.logStackTrace("Unable to read attributes of file: " + e1.toAbsolutePath(), e);
                        return 0;
                    }
                });

                while (archivedLogs.size() > ARD.getLogStockSize()) {
                    try {
                        if (Files.deleteIfExists(archivedLogs.get(0))) {
                            this.log(archivedLogs.get(0).toAbsolutePath() + " has been deleted!");
                        } else {
                            this.error(archivedLogs.get(0).toAbsolutePath() + " was meant to be deleted, but it's missing! Something is not right...");
                        }
                    } catch (Exception e) {
                        this.logStackTrace("Failed to delete the log file " + archivedLogs.get(0).toAbsolutePath(), e);
                    }
                    archivedLogs.remove(archivedLogs.get(0));
                }
            }
        } else if (ARD.shouldStockpileLogs() && ARD.getLogStockSize() < 1) {
            this.log("Stockpiling of the logs is enabled! Stockpile limit is infinite!");
        }

        this.log("Post-Initialization of Logger finished!");
    }

    /**
     * Used to get boolean with the state of initialization of the Logger.
     *
     * @return {@link Boolean} true if logger has been initialized successfully, false otherwise.
     * @apiNote Has to be implemented manually.
     */
    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Custom Log method that allows to set level of log, message and attach throwable.
     * Available levels:
     * <ul>
     *     <li>0 | LOG</li>
     *     <li>1 | WARN</li>
     *     <li>2 | ERROR</li>
     *     <li>3 | CRITICAL</li>
     * </ul>
     * @param msg String message to log to a log file.
     * @param type Int between 0 and 3 specifying selected level. Defaults to 0.
     * @param throwable Exception to log. (Nullable)
     */
    public void logCustom(String msg, int type, @Nullable Throwable throwable) {
        if (Objects.nonNull(msg) && !msg.startsWith("[")) msg = "[Logger] " + msg;
        if (!isInitialized()) {
            this.preInitMessages.add(new PreInitMessage(msg, type, throwable));
            return;
        }

        String Type = switch (type) {
            case 1 -> "WARN";
            case 2 -> "ERROR";
            case 3 -> "CRITICAL";
            default -> "INFO";
        };

        if (crashed) {
            System.out.println("[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()) + "] [" + Type + "] " + msg);
            if (throwable != null) throwable.printStackTrace();
            return;
        }

        try {
            Files.writeString(this.LogFile, "[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()) + "] [" + Type + "] " + msg + "\n", StandardOpenOption.APPEND);
            if (throwable != null) {
                StackTraceElement[] stackTraceList = throwable.getStackTrace();
                StringBuilder stackTrace = new StringBuilder();

                for (StackTraceElement stackTraceElement : stackTraceList) stackTrace.append("\tat ").append(stackTraceElement).append("\n");

                Files.writeString(
                    this.LogFile,
                    "[" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(new Date()) + "] [" + Type + "] " +
                    throwable + "\n" + stackTrace, StandardOpenOption.APPEND
                );

                if (Objects.nonNull(throwable.getCause())) this.logStackTrace("Caused By:", throwable.getCause());
                for (Throwable throwable1 : throwable.getSuppressed()) this.logStackTrace("Contains suppressed exception:", throwable1);
            }
        } catch (NoSuchFileException e) {
            try {
                this.initLogger();
            } catch (Throwable ex) {
                this.crashInit();
                if (crashed) {
                    this.error("Failed generating new log file! Application will now exit.");
                    throw new RuntimeException("Failed generating log file!");
                }
            }
            this.error("Log file seems to had been deleted! Created another copy, but the rest of the log file has been lost.");
            this.error("Catching last message...");
            this.log(msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Used to hold log entries before initialization of the logger and creation of the log file.
     * @param msg Message content
     * @param type Message Type
     * @param ex Throwable, if any.
     */
    private record PreInitMessage(String msg, int type, Throwable ex) {}

    /**
     * Used to disable Logger and remove the log file.
     * @throws IOException when log deletion failed.
     * @deprecated This functionality is removed, as this is a proper app that requires the creation of custom directories,
     * so disabling of the logger is not necessary or even useful.
     */
    @Deprecated(since = "0.0.1-DEV", forRemoval = true)
    public void exit() throws IOException {
//        System.out.println("LOGGER WAS DISABLED. If any errors occur they will not be logged and can be not shown in the console! Use at your own risk.");
//        this.disabled = true;
//        Files.readAllLines(this.LogFile).forEach(System.out::println);
//        Files.deleteIfExists(this.LogFile);
//        this.LogFile = null;
    }
}
