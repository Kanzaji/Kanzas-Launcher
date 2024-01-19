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

import java.util.List;

public class InstallationManager implements IService {
    private static final LoggerCustom logger = new LoggerCustom("Installation Manager");
    @Override
    public String getName() {
        return "Installation Manager";
    }

    /**
     * Used to get lists of implemented phases.
     * Only phases mentioned in a returned list are executed, even if implemented.
     * @return List with implemented initialization phases.
     */
    @Override
    public List<ServiceManager.State> getPhases() {
        return List.of(ServiceManager.State.INIT, ServiceManager.State.PRE_INIT, ServiceManager.State.POST_INIT);
    }

    /**
     * Used for PreInit phase of the service.
     * @throws Throwable Possible exception from the PreInit phase.
     */
    @Override
    public void preInit() throws Throwable {
        logger.log("Pre-Initialization of installation manager started.");
    }

    /**
     * Used for Init phase of the service.
     * @throws Throwable Possible exception from the Init phase.
     */
    @Override
    public void init() throws Throwable {}

    /**
     * Used for PostInit phase of the service.
     * @throws Throwable Possible exception from the PostInit phase.
     */
    @Override
    public void postInit() throws Throwable {}

    // TODO: Add App Verification
    public boolean verifyAppJar() {
        logger.log("Verifying App jar for corruption...");
        return true;
    }
}