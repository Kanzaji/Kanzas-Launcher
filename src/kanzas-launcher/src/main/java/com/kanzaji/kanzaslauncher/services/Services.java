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

package com.kanzaji.kanzaslauncher.services;

import com.kanzaji.kanzaslauncher.KanzasLauncher;
import com.kanzaji.kanzaslauncher.services.configuration.ConfigurationKey;
import com.kanzaji.kanzaslauncher.services.configuration.ConfigurationService;

import java.nio.file.Path;

public enum Services {
    // Services enum, for easier access to services when needed.
    LOGGER(Logger.getInstance().getName()),
    CONFIG("Main Configuration Service"),
    CLI("CLI Handler"),
    GUI("GUI Service");

    // Fields used by ServiceManager to get services.
    public final String name;
    Services(String name) {
        this.name = name;
    }

    // Other methods related to services

    /**
     * Used to register services to ServiceManager.
     * @apiNote Even tho it's possible to register new service anywhere,
     * and the ENUM isn't required, for consistency reasons,
     * new services should be registered here with added ENUM entry.
     */
    public static void registerServices() {
        ServiceManager.registerService(Logger.getInstance());

        ConfigurationService mainCFG = new ConfigurationService("Main Configuration Service", Path.of("Kanza's-Launcher Config.json5"));
        ServiceManager.registerService(mainCFG);
        mainCFG.registerKey(new ConfigurationKey("App-Mode", () -> {
            if (
                KanzasLauncher.ARGUMENTS.contains("-forceconsole") ||
                System.console() != null &&
                !KanzasLauncher.ARGUMENTS.contains("-forcegui")
            ) return "CLI";
            return "GUI";
        }, "mode", "Sets mode of the application. Can be: CLI / GUI"));
        mainCFG.registerKey(new ConfigurationKey("Test1", () -> "", "2", "test"));
        mainCFG.registerKey(new ConfigurationKey("Test2", () -> "", "1", "test2"));

        ServiceManager.registerService(new CLIHandler());
    }
}
