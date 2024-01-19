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

package com.kanzaji.catdownloaderlegacy;

import com.kanzaji.catdownloaderlegacy.guis.GUIUtils;
import com.kanzaji.catdownloaderlegacy.loggers.LoggerCustom;
import com.kanzaji.catdownloaderlegacy.registry.ArgumentHandlerRegistry;
import com.kanzaji.catdownloaderlegacy.registry.Registries;
import com.kanzaji.catdownloaderlegacy.utils.*;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;


/**
 * Main class holding Global variables for the app and containing the main method.
 * @see KanzasLauncher#main(String[])
 */
public final class KanzasLauncher {
    // Launch fresh instances of required utilities.
    private static final LoggerCustom logger = new LoggerCustom("Main");
    private static final Gson gson = new Gson();
    static final ArgumentHandlerRegistry AHR = ArgumentHandlerRegistry.getInstance();

    // Global variables
    public static final String VERSION = "0.0.1-DEVELOP";
    public static final String REPOSITORY = "https://github.com/Kanzaji/Kanzas-Launcher";
    public static final String NAME = "Kanza's Launcher";
    /**
     * Path to the java environment running the app.
     */
    public static Path JAVAPATH = null;
    /**
     * Path to the .jar containing the app.
     */
    public static Path APPPATH = null;
    /**
     * Path to the working directory.
     */
    public static Path WORKPATH = null;
    /**
     * Arguments passed to the app.
     */
    public static String[] ARGUMENTS = null;

    /**
     * Main method of the app.
     * @param args String[] arguments for the app.
     */
    public static void main(String[] args) {
        ARGUMENTS = args;

        try {
            Services.init();
            Services.postInit();

            RandomUtils.closeTheApp(0);
        } catch (Exception | Error e) {
            System.out.println("---------------------------------------------------------------------");
            System.out.println("Kanza's Launcher crashed! More details are in the log file at \"" + logger.getLogPath() + "\".");
            logger.logStackTrace("Exception thrown while executing main app code!", e);
            RandomUtils.closeTheApp(1);
        }
    }

    private static class Services {
        private static final LoggerCustom logger = new LoggerCustom("Main.Services");

        /**
         * This method is used to initialize requires services, utilities and global "utility" variables.
         * @throws IOException when IO Exception occurs.
         */
        public static void init() throws IOException {
            logger.init();
            logger.log(NAME + " version: " + VERSION);

            try {
                APPPATH = Path.of(KanzasLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceFirst("/", ""));
                logger.log("App Path: " + APPPATH.toAbsolutePath());
            } catch (Exception e) {
                logger.logStackTrace("Failed to get App directory!", e);
            }

            try {
                JAVAPATH = Path.of(ProcessHandle.current().info().command().orElseThrow());
                logger.log("Java Path: " + JAVAPATH.toAbsolutePath());
            } catch (Exception e) {
                logger.logStackTrace("Failed to get Java directory!", e);
            }

            GUIUtils.setLookAndFeel();
            // All arguments should be decoded in the AHR.
            // However, this method Overrides arguments, so it is required to run before AHR decoding.
            if (Arrays.stream(ARGUMENTS).toList().contains("-PostUpdateRoutine")) Updater.updateCleanup();

            System.out.println("---------------------------------------------------------------------");
            System.out.println("     " + NAME + " " + VERSION);
            System.out.println("     Created by: Kanzaji");
            System.out.println("---------------------------------------------------------------------");

            Registries.setup();

            AHR.decodeArguments(ARGUMENTS);
            AHR.printConfiguration("Program Configuration from Arguments:");

            if (AHR.areSettingsEnabled()) SettingsManager.initSettings();

            WORKPATH = Path.of(AHR.getWorkingDir());
            System.out.println("Running in " + WORKPATH.toAbsolutePath());
        }

        /**
         * This method is used to launch all post-init methods of services.
         * @throws IOException when IO Exception occurs.
         */
        public static void postInit() throws IOException {
            logger.postInit();

            logger.log("Checking network connection...");
            if (AHR.isBypassNetworkCheckActive()) {
                logger.warn("Network Bypass active! Be aware, Un-intended behaviour due to missing network connection is possible!");
            } else {
                long StartingTime = System.currentTimeMillis();
                if (NetworkingUtils.checkConnection("https://github.com/")) {
                    float CurrentTime = (float) (System.currentTimeMillis() - StartingTime) / 1000F;
                    logger.log("Network connection checked! Time to verify network: " + CurrentTime + " seconds.");
                    if (CurrentTime > 2) {
                        logger.print("It appears you have slow network connection! This might or might not cause issues with Verification or Download steps. Use with caution.", 1);
                    }
                } else {
                    logger.critical("No network connection! This app can not run properly without access to the internet.");
                    System.out.println("It appears you are running this app without access to the internet. This app requires internet connection to function properly.");
                    System.out.println("If you have network connection, and the Check host is unavailable (github.com), run the app with -BypassNetworkCheck argument!");
                    RandomUtils.closeTheApp(2);
                }
            }

            Updater.checkForUpdates();

            // Redirects the entire output of any Logger to a console!
            if (!AHR.isLoggerActive()) logger.exit();
        }
    }
}