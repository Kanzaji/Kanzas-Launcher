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

package com.kanzaji.kanzaslauncher.services.configuration.enums;

import com.kanzaji.kanzaslauncher.services.ServiceManager;
import com.kanzaji.kanzaslauncher.services.Services;
import com.kanzaji.kanzaslauncher.services.configuration.ConfigurationService;

public enum MainCFG {

    /**
     * Holds information about the mode of the app. Can be CLI or GUI.
     * @apiNote Class: {@link String}
     */
    MODE("App-Mode");

    // Fields and Methods.
    public final String name;
    MainCFG(String name) {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) ServiceManager.<ConfigurationService>get(Services.CONFIG).getValue(this.name);
    }

    // Static helper methods.
    public static boolean isConsole() {return MODE.getValue().equals("CLI");}
    public static boolean isGUI() {return !isConsole();}
}
