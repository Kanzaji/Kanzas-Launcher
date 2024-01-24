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

package com.kanzaji.kanzaslauncher;

import com.kanzaji.kanzaslauncher.services.*;
import com.kanzaji.kanzaslauncher.services.configuration.enums.MainCFG;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KanzasLauncher {
    public static final String VERSION = "0.0.1-DEVELOP";
    @SuppressWarnings("ConstantConditions")
    public static final boolean DEVELOP = VERSION.endsWith("DEVELOP");
    @SuppressWarnings("ConstantConditions")
    public static final boolean SNAPSHOT = VERSION.endsWith("SNAPSHOT");
    public static final List<String> ARGUMENTS = new ArrayList<>();
    private static final LoggerCustom logger = new LoggerCustom("Main");

    public static void main(String[] args) {
        ARGUMENTS.addAll(Arrays.stream(args).toList());
        try {
            logger.log("Kanza's launcher " + VERSION);
            Services.registerServices();

            ServiceManager.runPreInit();
            ServiceManager.runInit();

            // For some reason, the console object is null when running this from IntelliJ. When running in IntelliJ, add -ForceConsole argument.
            // Some features, like password hiding, will not be available in DEV environment because of that.
            // @see https://youtrack.jetbrains.com/issue/IDEA-18814/IDEA-doesnt-work-with-System.console
            if (MainCFG.isGUI()) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    logger.logStackTrace("Look And Feel not available! Going back to default.", e);
                }

                JOptionPane.showConfirmDialog(
                        null,
                        "Kanza's Launcher GUI is W.I.P. Please launch the application from the Command line for usage of CLI.",
                        "Kanza's Launcher",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );

                throw new IllegalStateException("GUI mode is WIP.");
            }

            ServiceManager.runPostInit();
            ServiceManager.runExit();
        } catch (Throwable e) {
            if (!logger.isInitialized()) Logger.getInstance().crashInit();
            logger.logStackTrace("Kanza's Launcher crashed!", e);
            System.out.println("Kanza's Launcher crashed! Exception: \"" + e.getMessage() + "\"! For more details, check the log file at: \n" + logger.getLogPath());
            ServiceManager.runCrash();
        }
    }
}
