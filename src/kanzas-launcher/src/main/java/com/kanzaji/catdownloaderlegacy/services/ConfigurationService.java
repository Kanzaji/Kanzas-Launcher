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

package com.kanzaji.catdownloaderlegacy.services;

import com.kanzaji.catdownloaderlegacy.loggers.LoggerCustom;
import org.jetbrains.annotations.Contract;

public class ConfigurationService implements IService {

    private static final class InstanceHolder {private static final ConfigurationService instance = new ConfigurationService();}
    private static final LoggerCustom logger = new LoggerCustom("Configuration Service");
    private ConfigurationService() {}

    /**
     * Method used to get instance of Configuration Service.
     * @return Instance of Configuration Service.
     */
    @Contract(pure = true)
    public static ConfigurationService getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * Method used to launch implementation of the Service.
     * Should not be called more than once, and action should be taken if invoked on already started service.
     * Effects depend heavily on the implementation of this Interface.
     */
    @Override
    public void StartService() {
        logger.log("Starting Configuration Service running on MAIN Thread...");
        // do configuration service. Literally I have no idea what I'm currently doing lmao. I need to hold and decode Arguments and Settings here, yet I have no idea what I'm doing.
    }

    /**
     * Method used to stop implementation of the Service.
     * Depends on the Implementation and might not always do anything.
     */
    @Override
    public void StopService() {
        logger.log("Stopping Configuration Service...");
    }

    /**
     * Method used to get Thread created by StartService(), if Service is running on separate thread.;
     *
     * @return Null, this method is not implemented in this implementation of IService Interface.
     */
    @Override
    @Deprecated(since = "0.0.1", forRemoval = false)
    public Thread getThread() {
        return null;
    }
}
